package com.youibot.docrobo.robo.common.infra;

public class TraceTaskScheduler extends WrapTaskScheduler {
	public TraceTaskScheduler(TaskScheduler wrap) {
		super(wrap);
	}

	@Override
	public void reschedule(Task task) {
		trace("reschedule " + task.dump(true));
		
		super.reschedule(task);
	}
	
	private final void trace(String msg) {

	}
}
