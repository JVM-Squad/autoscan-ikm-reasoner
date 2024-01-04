
package org.semanticweb.elk.reasoner.saturation.context;


import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;

/**
 * Keeps statistics about the number of created and modified contexts
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContextStatistics {

	/**
	 * The number of created contexts
	 */
	public int countCreatedContexts;
	
	/**
	 * the number of times a context has been processed
	 */
	public int countProcessedContexts;

	public int countModifiedContexts;

	/**
	 * the time spent on processing
	 */
	public long timeContextProcess;
	
	private int addCounter_ = 0;

	/**
	 * Reset all timers to zero.
	 */
	public void reset() {
		addCounter_ = 0;
		countCreatedContexts = 0;
		countProcessedContexts = 0;
		countModifiedContexts = 0;
		timeContextProcess = 0;
	}

	public synchronized void add(ContextStatistics stats) {
		addCounter_++;
		countCreatedContexts += stats.countCreatedContexts;
		countProcessedContexts += stats.countProcessedContexts;
		countModifiedContexts += stats.countModifiedContexts;
		timeContextProcess += stats.timeContextProcess;
	}

	public void print(Logger logger, LogLevel level) {
		if (!logger.isDebugEnabled() || addCounter_ <= 0)
			return;

		if (countCreatedContexts > 0) {
			LoggerWrap.log(logger, level, "Contexts created: " + countCreatedContexts);
		}
		
		if (countProcessedContexts > 0) {
			LoggerWrap.log(logger, level, "Contexts processed: " + countProcessedContexts + " (" +
					(timeContextProcess / addCounter_) + " ms)");
		}
		
		if (countModifiedContexts > 0) {
			LoggerWrap.log(logger, level, "Contexts modified: " + countModifiedContexts);
		}
	}
	
	public void check(Logger logger) {
		if (countCreatedContexts > countProcessedContexts) {
			logger.error("More contexts than context activations!");
		}
	}
	
}
