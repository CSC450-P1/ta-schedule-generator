/*
Rev Name   Date      Description

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

public class TAScheduler implements Runnable {
	
	private static final long MAX_RUNTIME_MILLISECONDS = 14800l;
	private static final long SLEEP_TIME_MILLISECONDS = 100l;
	
	private static final int MAX_SCHEDULES = 30;
	
	private static final int MAX_THREADS = Math.max(4, Runtime.getRuntime().availableProcessors());
	private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(MAX_THREADS, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			final Thread thread = new Thread(r);
			thread.setDaemon(true);
			return thread;
		}
	});
	
	public static Future<?> schedule(final List<TA> tas, final List<Course> courses, final Consumer<List<Schedule>> callback, final Consumer<Exception> errorCallback) {
		final List<Activity> activities = new ArrayList<>();
		for(final Course course : courses) {
			final List<Activity> courseActivities = course.getActivities();
			for(final Activity activity : courseActivities) {
				activity.setCourse(course);
			}
			activities.addAll(courseActivities);
		}
		return THREAD_POOL.submit(new TAScheduler(tas, activities, callback, errorCallback));
	}
	
	private final List<TA> tas;
	private final List<Activity> activities;
	private final long startTime = System.currentTimeMillis();
	private final Consumer<List<Schedule>> callback;
	private final Consumer<Exception> errorCallback;
	
	public TAScheduler(final List<TA> tas, final List<Activity> activities, final Consumer<List<Schedule>> callback, final Consumer<Exception> errorCallback) {
		super();
		this.tas = tas;
		this.activities = activities;
		this.callback = callback;
		this.errorCallback = errorCallback;
	}

	@Override
	public void run() {
		final List<Future<?>> threads = new ArrayList<>(MAX_THREADS - 1);
		final List<CompletableFuture<List<Schedule>>> futures = new ArrayList<>(MAX_THREADS - 1);
		try {
			for(int i = 0; i < MAX_THREADS - 1; i++) {
				final CompletableFuture<List<Schedule>> future = new CompletableFuture<>();
				futures.add(future);
				threads.add(THREAD_POOL.submit(new GeneticAlgorithm(tas, activities, future)));
			}
			while(System.currentTimeMillis() - startTime < MAX_RUNTIME_MILLISECONDS) {
				try {
					if(Thread.currentThread().isInterrupted()) {
						throw new InterruptedException();
					}
					Thread.sleep(SLEEP_TIME_MILLISECONDS);
				} catch (InterruptedException e) {
					for(final Future<?> thread : threads) {
						thread.cancel(true);
					}
					return;
				}
			}
			for(final Future<?> thread : threads) {
				thread.cancel(true);
			}
			final HashSet<Schedule> bestSchedules = new LinkedHashSet<>();
			for(final CompletableFuture<List<Schedule>> future : futures) {
				bestSchedules.addAll(future.get());
			}
			List<Schedule> sortedBestSchedules = new ArrayList<>(bestSchedules);
			sortedBestSchedules.sort((s1, s2) -> s1.getError() - s2.getError());
			if(sortedBestSchedules.size() > MAX_SCHEDULES) {
				sortedBestSchedules = sortedBestSchedules.subList(0, MAX_SCHEDULES);
			}
			callback.accept(sortedBestSchedules);
		} catch(Exception e) {
			e.printStackTrace();
			errorCallback.accept(e);
		}
	}

}
