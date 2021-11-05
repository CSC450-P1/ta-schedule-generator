package edu.missouristate.taschedulegenerator.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import edu.missouristate.taschedulegenerator.domain.Activity;
import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.Schedule;
import edu.missouristate.taschedulegenerator.domain.TA;

public class TAScheduler implements Callable<List<Schedule>> {
	
	private static final long MAX_RUNTIME_MILLISECONDS = 14500l;
	private static final long SLEEP_TIME_MILLISECONDS = 100l;
	
	private static final int MAX_THREADS = 4;
	private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(MAX_THREADS, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			final Thread thread = new Thread(r);
			thread.setDaemon(true);
			return thread;
		}
	});
	
	public static Future<List<Schedule>> schedule(final List<TA> tas, final List<Course> courses) {
		final List<Activity> activities = new ArrayList<>();
		for(final Course course : courses) {
			activities.addAll(course.getActivities());
		}
		return THREAD_POOL.submit(new TAScheduler(tas, activities));
	}
	
	private final List<TA> tas;
	private final List<Activity> activities;
	private final long startTime = System.currentTimeMillis();
	
	public TAScheduler(final List<TA> tas, final List<Activity> activities) {
		super();
		this.tas = tas;
		this.activities = activities;
	}

	@Override
	public List<Schedule> call() {
		final List<Future<List<Schedule>>> threads = new ArrayList<>(MAX_THREADS - 1);
		for(int i = 0; i < MAX_THREADS - 1; i++) {
			threads.add(THREAD_POOL.submit(new GeneticAlgorithm(tas, activities)));
		}
		while(System.currentTimeMillis() - startTime < MAX_RUNTIME_MILLISECONDS) {
			try {
				Thread.sleep(SLEEP_TIME_MILLISECONDS);
			} catch (InterruptedException e) {
				break;
			}
		}
		for(final Future<List<Schedule>> thread : threads) {
			thread.cancel(true);
		}
		final HashSet<Schedule> bestSchedules = new LinkedHashSet<>();
		for(final Future<List<Schedule>> thread : threads) {
			try {
				bestSchedules.addAll(thread.get());
			} catch (InterruptedException | ExecutionException e) {
				System.err.println("Error getting results from thread:");
				e.printStackTrace();
			}
		}
		final List<Schedule> sortedBestSchedules = new ArrayList<>(bestSchedules);
		sortedBestSchedules.sort((s1, s2) -> s1.getError() - s2.getError());
		return sortedBestSchedules;
	}

}
