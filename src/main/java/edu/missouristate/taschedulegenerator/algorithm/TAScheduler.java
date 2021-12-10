/*
 * MIT License
 * 
 * Copyright (c) 2021 Missouri State University
 * 
 * See LICENSE in the project's root directory for full license details
 * 
 */
package edu.missouristate.taschedulegenerator.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

import edu.missouristate.taschedulegenerator.domain.Activity;
import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.Schedule;
import edu.missouristate.taschedulegenerator.domain.TA;

/**
 * Allows for creating multiple genetic algorithms, timing them, and collecting the best results from each.
 * 
 * SDD 3.2.2
 * 
 * @author Noah Geren
 *
 */
public class TAScheduler implements Runnable {
	
	/**
	 * The max amount of milliseconds before interrupting genetic algorithms.
	 */
	private static final long MAX_RUNTIME_MILLISECONDS = 14750l;
	/**
	 * The time to sleep in milliseconds between checking if past max runtime.
	 */
	private static final long SLEEP_TIME_MILLISECONDS = 100l;
	
	/**
	 * The maximum number of schedules to be returned.
	 */
	private static final int MAX_SCHEDULES = 30;
	
	/**
	 * The max number of threads to use for timing and genetic algorithms.
	 */
	private static final int MAX_THREADS = Math.max(4, Runtime.getRuntime().availableProcessors());
	/**
	 * The thread pool used for timing and genetic algorithm threads.
	 */
	private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(MAX_THREADS, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			final Thread thread = new Thread(r);
			thread.setDaemon(true);
			return thread;
		}
	});
	
	/**
	 * Starts scheduling TAs and courses.
	 * 
	 * @param tas The TAs to schedule.
	 * @param courses The courses to schedule.
	 * @param callback The callback function for when generating schedules completes.
	 * @param errorCallback The callback function used when an uncaught expection is thrown.
	 * @return A Future object that can be used to cancel generating schedules.
	 */
	public static Future<?> schedule(final List<TA> tas, final List<Course> courses, final Consumer<List<Schedule>> callback, final Consumer<Exception> errorCallback) {
		final List<Activity> activities = new ArrayList<>();
		// Change list of courses into list of activities
		for(final Course course : courses) {
			final List<Activity> courseActivities = course.getActivities();
			for(final Activity activity : courseActivities) {
				activity.setCourse(course);
			}
			activities.addAll(courseActivities);
		}
		// Start new timer thread
		return THREAD_POOL.submit(new TAScheduler(tas, activities, callback, errorCallback));
	}
	
	/**
	 * TAs used for generating schedules.
	 */
	private final List<TA> tas;
	/**
	 * Activities used for generting schedules.
	 */
	private final List<Activity> activities;
	/**
	 * The time generating started
	 */
	private final long startTime = System.currentTimeMillis();
	/**
	 * The callback function for when generating schedules completes.
	 */
	private final Consumer<List<Schedule>> callback;
	/**
	 * The callback function used when an uncaught expection is thrown.
	 */
	private final Consumer<Exception> errorCallback;
	
	/**
	 * Creates a new TAScheduler.
	 * 
	 * @param tas TAs used for generating schedules.
	 * @param activities Activities used for generting schedules.
	 * @param callback The callback function for when generating schedules completes.
	 * @param errorCallback The callback function used when an uncaught expection is thrown.
	 */
	public TAScheduler(final List<TA> tas, final List<Activity> activities, final Consumer<List<Schedule>> callback, final Consumer<Exception> errorCallback) {
		super();
		this.tas = tas;
		this.activities = activities;
		this.callback = callback;
		this.errorCallback = errorCallback;
	}

	/**
	 * Starts the timer and genetic algorithm threads.
	 * <strong>Order of execution:</strong>
	 * <ol>
	 * <li>Start genetic algorithm threads</li>
	 * <li>Sleep for SLEEP_TIME_MILLISECONDS until reaching MAX_RUNTIME_MILLISECONDS</li>
	 * <li>Interrupt genetic algorithm threads</li>
	 * <li>Collect best results from each genetic algorithm</li>
	 * <li>Sort and return the MAX_SCHEDULES best schedules</li>
	 * </ol>
	 * 
	 * @see MAX_THREADS
	 * @see MAX_RUNTIME_MILLISECONDS
	 * @see SLEEP_TIME_MILLISECONDS
	 * @see MAX_SCHEDULES
	 */
	@Override
	public void run() {
		final List<Future<?>> threads = new ArrayList<>(MAX_THREADS - 1);
		final List<CompletableFuture<List<Schedule>>> futures = new ArrayList<>(MAX_THREADS - 1);
		try {
			// Create a new thread for each genetic algorithm
			for(int i = 0; i < MAX_THREADS - 1; i++) {
				final CompletableFuture<List<Schedule>> future = new CompletableFuture<>();
				futures.add(future);
				threads.add(THREAD_POOL.submit(new GeneticAlgorithm(tas, activities, future)));
			}
			// Wait untile MAX_RUNTIME_MILLISECONDS has ellapsed
			while(System.currentTimeMillis() - startTime < MAX_RUNTIME_MILLISECONDS) {
				try {
					if(Thread.currentThread().isInterrupted()) {
						throw new InterruptedException();
					}
					Thread.sleep(SLEEP_TIME_MILLISECONDS);
				} catch (InterruptedException e) {
					for(final Future<?> thread : threads) { // If cancelled then cancel all genetic algorithms
						thread.cancel(true);
					}
					return;
				}
			}
			// Interrupt each genetic algorithm
			for(final Future<?> thread : threads) {
				thread.cancel(true);
			}
			// Collect the best schedules from each genetic algorithm while removing duplicates
			final HashSet<Schedule> bestSchedules = new LinkedHashSet<>();
			for(final CompletableFuture<List<Schedule>> future : futures) {
				bestSchedules.addAll(future.get());
			}
			// Sort best schedules
			List<Schedule> sortedBestSchedules = new ArrayList<>(bestSchedules);
			sortedBestSchedules.sort((s1, s2) -> s1.getError() - s2.getError());
			// Limit count to MAX_SCHEDULES
			if(sortedBestSchedules.size() > MAX_SCHEDULES) {
				sortedBestSchedules = sortedBestSchedules.subList(0, MAX_SCHEDULES);
			}
			// Send best schedules to callback function
			callback.accept(sortedBestSchedules);
		} catch(Exception e) {
			// An error occurred
			e.printStackTrace();
			// Cancel genetic algorithms
			for(final Future<?> thread : threads) {
				thread.cancel(true);
			}
			// Send exception to error callback
			errorCallback.accept(e);
		}
	}

}
