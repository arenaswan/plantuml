/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 * 
 *
 */
package net.sourceforge.plantuml.project3;

public class TaskInstant implements Complement {

	private final Moment task;
	private final TaskAttribute attribute;
	private final int delta;

	public TaskInstant(Moment task, TaskAttribute attribute) {
		this(task, attribute, 0);
	}

	private TaskInstant(Moment task, TaskAttribute attribute, int delta) {
		this.task = task;
		this.attribute = attribute;
		this.delta = delta;
		if (attribute != TaskAttribute.START && attribute != TaskAttribute.END) {
			throw new IllegalArgumentException();
		}
	}

	public TaskInstant withDelta(int newDelta) {
		return new TaskInstant(task, attribute, newDelta);
	}

	private Instant manageDelta(Instant value) {
		if (delta > 0) {
			for (int i = 0; i < delta; i++) {
				value = value.increment();
			}
		}
		if (delta < 0) {
			for (int i = 0; i < -delta; i++) {
				value = value.decrement();
			}
		}
		return value;
	}

	public Instant getInstantPrecise() {
		if (attribute == TaskAttribute.START) {
			return manageDelta(task.getStart());
		}
		if (attribute == TaskAttribute.END) {
			return manageDelta(task.getEnd().increment());
		}
		throw new IllegalStateException();
	}

	public Instant getInstantTheorical() {
		if (attribute == TaskAttribute.START) {
			return manageDelta(task.getStart());
		}
		if (attribute == TaskAttribute.END) {
			return manageDelta(task.getEnd());
		}
		throw new IllegalStateException();
	}

	@Override
	public String toString() {
		return attribute.toString() + " of " + task;
	}

	public final Moment getMoment() {
		return task;
	}

	public final boolean isTask() {
		return task instanceof Task;
	}

	public final TaskAttribute getAttribute() {
		return attribute;
	}

}
