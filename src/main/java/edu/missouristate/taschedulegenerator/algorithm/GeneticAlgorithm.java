package edu.missouristate.taschedulegenerator.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import edu.missouristate.taschedulegenerator.domain.Activity;
import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.Schedule;
import edu.missouristate.taschedulegenerator.domain.Schedule.ScheduledActivity;
import edu.missouristate.taschedulegenerator.domain.TA;

/**
 * This class represents an independent genetic algorithm that can easily be ran on a new thread.
 * <em>Note: The genetic algorithm will continue to run until the thread it is running on is interrupted.</em>
 * 
 * @author Noah Geren
 *
 */
public class GeneticAlgorithm implements Runnable, Comparator<int[]> {
	/**
	 * How many individuals should be in a population
	 */
	private static final int POPULATION_SIZE = 250;
	/**
	 * The number of random individuals to use for tournament selection
	 */
	private static final int TOURNAMENT_SIZE = 25;
	/**
	 * The number of indivduals that should be treated as "elite" (not changed during operations)
	 */
	private static final int ELITE_COUNT = 5;
	/**
	 * The rate at which genes should be exchanged during crossover
	 */
	private static final double CROSSOVER_RATE = 0.5;
	/**
	 * The rate at which genes should be mutated
	 */
	private static final double MUTATION_RATE = 0.1;
	/**
	 * The standard deviation from the amount of hours an activity needs (used for generateRandomSchedule)
	 */
	private static final double HOURS_STANDARD_DEVIATION = 2.0;
	/**
	 * The maximum number of schedules to return when complete
	 */
	private static final int SCHEDULE_RETURN_COUNT = 10;
	
	// Error multipliers
	
	// Hard scheduling constraints
	/**
	 * TA got assigned to sections which have overlapping TAs
	 */
	private static final int SECTION_OVERLAP = 1000;
	/**
	 *  TA got assigned too many hours
	 */
	private static final int TA_OVER_HOURS = 500;
	
	// Soft scheduling constraints
	/**
	 * TA got assigned too few hours
	 */
	private static final int TA_UNDER_HOURS = 125;
	/**
	 * Activity got assigned too few hours
	 */
	private static final int ACTIVITY_UNDER_HOURS = 100;
	/**
	 * Activity got assigned too many hours
	 */
	private static final int ACTIVITY_OVER_HOURS = 50;
	/**
	 * A single course's activities were assigned to different TAs
	 */
	private static final int NOT_ALL_SAME_TA = 50;
	/**
	 * List of TAs
	 */
	private final List<TA> tas;
	/**
	 * List of activities
	 */
	private final List<Activity> activities;
	/**
	 * Maps each activity to the TAs that are able to teach it
	 */
	private final Map<Integer, List<Integer>> tasByActivity = new HashMap<>();
	/**
	 * The length of each individual's genes
	 */
	private final int geneLength;
	/**
	 * Used to return results after being interrupted
	 */
	private final CompletableFuture<List<Schedule>> completableFuture;
	/**
	 * The ratio of total TA hours needed to total activity hours needed 
	 */
	private final double taActivityHoursRatio;
	/**
	 * Random number generator
	 */
	private final Random random = new Random();
	
	/**
	 * Create a new genetic algorithm and calculate which activities can be assisted by which TAs.
	 * @param tas The TAs used for generating schedules.
	 * @param activities The activities used for generating schedules.
	 * @param completableFuture The Future that the results will be returned to after being interrupted.
	 * @throws NoTAAvailableException Thrown if there exists an activity for which no TA is available to assist.
	 */
	public GeneticAlgorithm(final List<TA> tas, final List<Activity> activities, final CompletableFuture<List<Schedule>> completableFuture) throws NoTAAvailableException {
		super();
		this.tas = tas;
		this.activities = activities;
		this.geneLength = activities.size() * 2 + 1;
		this.completableFuture = completableFuture;
		
		int activityHours = 0;
		int taHours = 0;
		// Loop through each activity and each TA to find which are compatable (cartesian product)
		for(int activityIdx = 0; activityIdx < activities.size(); activityIdx++) {
			final Activity activity = activities.get(activityIdx);
			activityHours += activity.getHoursNeeded();
			final List<Integer> tasForActivity = new ArrayList<>();
			for(int taIdx = 0; taIdx < tas.size(); taIdx++) {
				final TA ta = tas.get(taIdx);
				if(activityIdx == 0) {
					taHours += ta.getMaxHours();
				}
				// If activity needs TA and if TA unavailable times don't intersect with activity time
				if((ta.isTA() || !activity.isMustBeTA()) &&
						(activity.getTime() == null || ta.getNotAvailable().stream().noneMatch(notAvailable -> notAvailable.intersects(activity.getTime())))) {
					tasForActivity.add(taIdx);
				}
			}
			// There are no TAs available to assist with this activity
			if(tasForActivity.isEmpty()) {
				throw new NoTAAvailableException(String.format("No TA available for %s %s", activity.getCourse().getCourseCode(), activity.getName()));
			}
			tasByActivity.put(activityIdx, tasForActivity);
		}
		// Prioritize TA hours over activity hours
		this.taActivityHoursRatio = taHours / (double) activityHours;
	}

	/**
	 * Starts the genetic algorithm. This should be called internally by the Thread class.
	 */
	@Override
	public void run() {
		// Create a random population
		int[][] population = new int[POPULATION_SIZE][];
		for(int i = 0; i < POPULATION_SIZE; i++) {
			population[i] = getRandomSchedule();
			repair(population[i]); // Repair random schedules to give a good starting point
		}
		// Sort the random schedules
		Arrays.sort(population, this);
		// Start of genetic algorithm loop. Won't finish until the thread is interrupted.
		while(!Thread.currentThread().isInterrupted()) {
			crossover(population);
			mutate(population);
			// Reset each individual's error count
			for(int i = 0; i < POPULATION_SIZE; i++) {
				population[i][geneLength - 1] = -1;
			}
			// Sort individuals from least error value to most error value
			Arrays.sort(population, this);
		}
		// Collect the best individuals (least error value) from the current population
		final LinkedHashSet<Schedule> bestSchedules = new LinkedHashSet<>();
		for(int i = 0; bestSchedules.size() < SCHEDULE_RETURN_COUNT && i < POPULATION_SIZE; i++) {
			repair(population[i]); // Repair to fix any final issues the genetic algorithm wasn't able to
			// Collect each scheduled activity for the current individual's schedule
			final List<ScheduledActivity> scheduledActivities = new ArrayList<>(activities.size());
			for(int j = 0; j < geneLength - 1; j += 2) {
				scheduledActivities.add(new ScheduledActivity(activities.get(j / 2), tas.get(population[i][j]), population[i][j + 1]));
			}
			final Schedule schedule = new Schedule(scheduledActivities);
			// Set schedule error value and populate error log
			schedule.setError(calculateScheduleError(population[i], schedule.getErrorLog()));
			// Add to LinkedHashSet to remove duplicates but preserve order
			bestSchedules.add(schedule);
		}
		// Complete the future with the list of the best schedules
		completableFuture.complete(new ArrayList<>(bestSchedules));
	}
	
	/**
	 * Combines the genes of each individual with another individual found using tournament selection.
	 * @param population The population on which crossover should be done.
	 * @see selectParent
	 * @see generateOffspring
	 * @see CROSSOVER_RATE
	 */
	private void crossover(final int[][] population) {
		for(int i = 0; i < population.length - ELITE_COUNT; i++) {
			if(i >= ELITE_COUNT) { // Do crossover
				generateOffspring(population[i], selectParent(population), CROSSOVER_RATE); // Use half of each parent's genes
			} else { // Do crossover but replace an individual on the opposite end of the population
				population[population.length - i - 1] = Arrays.copyOf(population[i], population[i].length);
				generateOffspring(population[population.length - i - 1], selectParent(population), CROSSOVER_RATE);
			}
		}
	}
	
	/**
	 * Uses tournament selection to select a parent for crossover.
	 * Tournament selection returns the best individual from a random subset of the population.
	 * 
	 * @param population The population from which the parent should be selected.
	 * @return The individual selected to be the parent.
	 * @see crossover
	 * @see TOURNAMENT_SIZE
	 */
	private int[] selectParent(final int[][] population) {
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < TOURNAMENT_SIZE; i++) {
			// The best individual is the one with the lowest index
			min = Math.min(min, random.nextInt(population.length));
		}
		return population[min];
	}
	
	/**
	 * Copies genes from parent two into parent one using the provided exchange rate.
	 * 
	 * @param parentOne Parent one. This parent's genes will be overwritten.
	 * @param parentTwo Parent two.
	 * @param exchangeRate The rate at when genes should be copied from parent two to parent one. Should be between 0 and 1.
	 */
	private void generateOffspring(final int[] parentOne, final int[] parentTwo, final double exchangeRate) {
		for(int i = 0; i < geneLength - 1; i++) {
			if(random.nextDouble() < exchangeRate) {
				parentOne[i] = parentTwo[i];
			}
		}
	}
	
	/**
	 * Mutates (slightly changes) each individual in the provided population.
	 * 
	 * @param population The population of individuals that should be mutated.
	 * @see getRandomSchedule
	 * @see generateOffspring
	 * @see MUTATION_RATE
	 */
	private void mutate(final int[][] population) {
		for(int i = ELITE_COUNT; i < population.length - ELITE_COUNT; i++) {
			generateOffspring(population[i], getRandomSchedule(), MUTATION_RATE); // Copy genes from a new random schedule
		}
	}
	
	/**
	 * Generates a random schedule using the tasByActivity map to only select available TAs and a guassian distribution to randomly select the hours assigned.
	 * 
	 * @return A random schedule represented by an integer array. Each pair of indices represent on activity with the first index being the TA's index in tas
	 * and the second index being the number of hours assigned.
	 * @see tasByActivity
	 * @see HOURS_STANDARD_DEVIATION
	 */
	private int[] getRandomSchedule() {
		final int[] schedule = new int[geneLength];
		int i = 0;
		// For each activity
		for(final Entry<Integer, List<Integer>> entry : tasByActivity.entrySet()) {
			// Randomly select an available TA
			schedule[i * 2] = entry.getValue().get(random.nextInt(entry.getValue().size()));
			// Randomly assign hours using a gaussian distribution
			schedule[i++ * 2 + 1] = (int) Math.round(Math.max(
					random.nextGaussian() * HOURS_STANDARD_DEVIATION
						+ activities.get(entry.getKey()).getHoursNeeded() * taActivityHoursRatio,
					1.0));
		}
		schedule[geneLength - 1] = -1;
		return schedule;
	}
	
	/**
	 * Calculates the a given schedule's error value by finding the sum of weighted scheduling constraint violations.
	 * <strong>Metrics Used:</strong>
	 * <ol>
	 * <li>If multiple of a TA's activities overlap.</li>
	 * <li>A TA was assigned more/fewer hours than their max hours.</li>
	 * <li>A activity was assigned more/fewer hours than needed.</li>
	 * <li>A single course's activities were split among multiple TAs.</li>
	 * </ol>
	 * 
	 * @param schedule The schedule to find the error value for.
	 * @param errorLog A list of strings that can be used to log error messages to. Can be null.
	 * @return The error value associated with the provided schedule.
	 * @see SECTION_OVERLAP
	 * @see ACTIVITY_UNDER_HOURS
	 * @see ACTIVITY_OVER_HOURS
	 * @see NOT_ALL_SAME_TA
	 * @see TA_OVER_HOURS
	 * @see TA_UNDER_HOURS
	 */
	private int calculateScheduleError(final int[] schedule, final List<String> errorLog) {
		final boolean log = errorLog != null;
		int error = 0;
		// Check if TA activities overlap
		for(int i = 0; i < geneLength - 3; i += 2) {
			for(int j = i + 2; j < geneLength - 2; j += 2) {
				if(schedule[i] == schedule[j]) {
					final Activity activityOne = activities.get(i / 2);
					final Activity activityTwo = activities.get(j / 2);
					if(activityOne.getTime() != null && activityTwo.getTime() != null
							&& activityOne.getTime().intersects(activityTwo.getTime())) {
						error += SECTION_OVERLAP;
						if(log) {
							errorLog.add(String.format("Activity %s - %s and %s - %s overlap", activityOne.getCourse().getCourseCode(), activityOne.getName(),
									activityTwo.getCourse().getCourseCode(), activityTwo.getName()));
						}
					}
				}
			}
		}
		final HashMap<Integer, Integer> taHours = new HashMap<>();
		final HashMap<Course, HashSet<Integer>> tasPerCourse = new HashMap<>();
		// Check if activities were over/under hours
		for(int i = 0; i < geneLength - 2; i += 2) {
			final Activity activity = activities.get(i / 2);
			final String activityName = String.format("%s - %s", activity.getCourse().getCourseCode(), activity.getName());
			Integer hours = taHours.getOrDefault(schedule[i], 0);	
			if(schedule[i + 1] > 0) {
				taHours.put(schedule[i], hours + schedule[i + 1]);
			}
			
			if(activity.getHoursNeeded() > schedule[i + 1]) {
				error += ACTIVITY_UNDER_HOURS * percentErrorMultiplier(activity.getHoursNeeded(), schedule[i + 1]);
				if(log) {
					errorLog.add(String.format("Activity %s under hours", activityName));
				}
			} else if(activity.getHoursNeeded() < schedule[i + 1]) {
				error += ACTIVITY_OVER_HOURS * percentErrorMultiplier(activity.getHoursNeeded(), schedule[i + 1]);
				if(log) {
					errorLog.add(String.format("Activity %s over hours", activityName));
				}
			}
			HashSet<Integer> tasForCourse = tasPerCourse.getOrDefault(activity.getCourse(), new HashSet<>());
			tasForCourse.add(schedule[i]);
			tasPerCourse.put(activity.getCourse(), tasForCourse);
		}
		// Check if course activities are assisted by all the same TA
		for(Entry<Course, HashSet<Integer>> tasForCourse : tasPerCourse.entrySet()) {
			error += NOT_ALL_SAME_TA * (tasForCourse.getValue().size() - 1);
			if(log && tasForCourse.getValue().size() > 1) {
				errorLog.add(String.format("Course %s has multiple TAs", tasForCourse.getKey().getCourseCode()));
			}
		}
		// Check if TA is over/under hours
		for(int i = 0; i < tas.size(); i++) {
			final TA ta = tas.get(i);
			final int hours = taHours.getOrDefault(i, 0);
			if(hours > ta.getMaxHours()) {
				error += TA_OVER_HOURS * percentErrorMultiplier(ta.getMaxHours(), hours);
				if(log) {
					errorLog.add(String.format("TA %s over hours", ta.getName()));
				}
			} else if(hours < ta.getMaxHours()) {
				error += TA_UNDER_HOURS * percentErrorMultiplier(ta.getMaxHours(), hours);
				if(log) {
					errorLog.add(String.format("TA %s under hours", ta.getName()));
				}
			}
		}
		return error >= 0 ? error : Integer.MAX_VALUE; // Just in case of integer overflow
	}
	
	/**
	 * Returns an error multiplier that accounts for the difference and percent error between expected and actual values.
	 * @param expected The value that was expected.
	 * @param actual The actual value that was used.
	 * @return |expected - actual| + |expected - actual|/expected
	 */
	private double percentErrorMultiplier(int expected, int actual) {
		return (double) Math.abs(expected - actual) + (Math.abs(expected - actual) / (double) expected);
	}

	/**
	 * Compares two schedules using their error value. Also sets the schedule's error value if it isn't already.
	 * 
	 * @param s1 Schedule one.
	 * @param s2 Schedule two.
	 * @return Negative if s1 is better than s2, positive if s2 is better than s1, and 0 if equal.
	 * @see calculateScheduleError
	 */
	@Override
	public int compare(int[] s1, int[] s2) {
		if(s1[geneLength - 1] == -1) {
			s1[geneLength - 1] = calculateScheduleError(s1, null);
		}
		if(s2[geneLength - 1] == -1) {
			s2[geneLength - 1] = calculateScheduleError(s2, null);
		}
		return s1[geneLength - 1] - s2[geneLength - 1];
	}
	
	/**
	 * Repairs a schedule by looking for common an easily fixable mistakes.
	 * Schedule problems fixed:
	 * <ul>
	 * <li>A TA has too many hours in one activity and too few in another</li>
	 * <li>Two TAs have an activity with same hours, but switching them would reduce the error value</li>
	 * </ul>
	 * 
	 * @param schedule The schedule to be repaired.
	 */
	private void repair(final int[] schedule) {
		Map<Integer, List<Integer>> activitiesByTA = new HashMap<>();
		for(int i = 0; i < geneLength - 2; i += 2) {
			List<Integer> activities = activitiesByTA.getOrDefault(schedule[i], new ArrayList<>());
			activities.add(i / 2);
			activitiesByTA.put(schedule[i], activities);
		}
		// TA has too many hours in one activity and too few in another
		for(List<Integer> activities : activitiesByTA.values()) {
			while(true) {
				int[] min = {Integer.MAX_VALUE, -1}, max = {Integer.MIN_VALUE, -1};
				for(int activityIdx : activities) {
					int diff = schedule[activityIdx * 2 + 1] - this.activities.get(activityIdx).getHoursNeeded();
					if(diff < min[0]) {
						min[0] = diff;
						min[1] = activityIdx * 2 + 1;
					}
					if(diff > max[0]) {
						max[0] = diff;
						max[1] = activityIdx * 2 + 1;
					}
				}
				if(min[0] >= 0 || max[0] <= 0) break;
				schedule[max[1]]--;
				schedule[min[1]]++;
			}
		}
		// Two TAs have an activity with same hours, but switching them would reduce NOT_ALL_SAME_TA error
		for(int i = 0; i < geneLength - 3; i += 2) {
			for(int j = i + 2; j < geneLength - 2; j += 2) {
				if(schedule[i] == schedule[j] || schedule[i + 1] != schedule[j + 1]) continue;
				if(schedule[geneLength - 1] == -1) {
					schedule[geneLength - 1] = calculateScheduleError(schedule, null);
				}
				int temp = schedule[i];
				schedule[i] = schedule[j];
				schedule[j] = temp;
				int newError = calculateScheduleError(schedule, null);
				if(newError < schedule[geneLength - 1]) {
					schedule[geneLength - 1] = newError;
				} else {
					temp = schedule[i];
					schedule[i] = schedule[j];
					schedule[j] = temp;
				}
			}
		}
	}
}
