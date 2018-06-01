/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
package net.sourceforge.fullsync;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fullsync.buffer.BlockBuffer;
import net.sourceforge.fullsync.impl.FillBufferTaskExecutor;

/**
 * This class should provide wrappers for most common synchronization tasks
 * like synchronizing a profile or perfoming a task tree.
 */
public class Synchronizer {
	private static final Logger logger = LoggerFactory.getLogger(Synchronizer.class);

	private final TaskGenerator taskGenerator;

	@Inject
	public Synchronizer(TaskGenerator taskGenerator) {
		this.taskGenerator = taskGenerator;
	}

	public synchronized TaskTree executeProfile(Profile profile, boolean interactive) {
		try {
			return taskGenerator.execute(profile, interactive);
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
		return null;
	}

	/**
	 * @return Returns the ErrorLevel
	 */
	public int performActions(TaskTree taskTree) {
		return performActions(taskTree, null);
	}

	/**
	 * TODO if we add some listener/feedback receiver here we could
	 * easily use this for visual action performing as well.
	 * -- done ?
	 *
	 * now we still need the action count info before everything is performed
	 * and later we'll need to cancel/stop the whole process
	 * looks like we really need to single thread the whole class !
	 *
	 * @return Returns the ErrorLevel
	 */
	public int performActions(TaskTree taskTree, TaskFinishedListener listener) {
		try {
			logger.info("Synchronization started");
			logger.info("  source:      " + taskTree.getSource().getConnectionDescription().getDisplayPath());
			logger.info("  destination: " + taskTree.getDestination().getConnectionDescription().getDisplayPath());

			BlockBuffer buffer = new BlockBuffer(logger);
			TaskExecutor queue = new FillBufferTaskExecutor(buffer);

			if (null != listener) {
				queue.addTaskFinishedListener(listener);
			}

			buffer.load();
			queue.enqueue(taskTree);
			queue.flush();
			buffer.unload();

			taskTree.getSource().flush();
			taskTree.getDestination().flush();
			taskTree.getSource().close();
			taskTree.getDestination().close();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
			logger.error("An Exception occured while performing actions", e);
			logger.info("synchronization failed");
			logger.info("------------------------------------------------------------");
			return 1;
		}
		logger.info("synchronization successful"); // TODO ...with x errors and y warnings
		logger.info("------------------------------------------------------------");
		return 0;
	}
}
