/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;

public class TestClassExpressionSaturation<J extends SaturationJob<? extends IndexedClassExpression>>
		extends ConcurrentComputation<J, ClassExpressionSaturationFactory<J>> {

	public TestClassExpressionSaturation(ComputationExecutor executor,
			int maxWorkers, SaturationState saturationState) {
		super(new ClassExpressionSaturationFactory<J>(
				new RuleApplicationAdditionFactory(saturationState),
				maxWorkers), executor, maxWorkers);
	}
	
	public TestClassExpressionSaturation(ComputationExecutor executor,
			int maxWorkers, OntologyIndex ontologyIndex) {
		super(new ClassExpressionSaturationFactory<J>(
				new RuleApplicationAdditionFactory(
						SaturationStateFactory
								.createSaturationState(ontologyIndex)),
				maxWorkers), executor, maxWorkers);
	}
}
