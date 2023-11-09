package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.DevTrace;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link SubsumerDecompositionRule} that processes
 * {@link IndexedObjectSomeValuesFrom} and produces the corresponding
 * {@link ForwardLink} or {@link BackwardLink} for the context corresponding to
 * its filler (or range filler).
 * 
 * @see IndexedObjectSomeValuesFrom#getFiller()
 * @see IndexedObjectSomeValuesFrom#getRangeFiller()
 * 
 * @author "Yevgeny Kazakov"
 */
public class IndexedObjectSomeValuesFromDecomposition
		extends AbstractSubsumerDecompositionRule<IndexedObjectSomeValuesFrom> {

	private static final Logger LOG = LoggerFactory.getLogger(IndexedObjectSomeValuesFromDecomposition.class);

	public static final String NAME = "IndexedObjectSomeValuesFrom Decomposition";

	private static SubsumerDecompositionRule<IndexedObjectSomeValuesFrom> INSTANCE_ = new IndexedObjectSomeValuesFromDecomposition();

	public static SubsumerDecompositionRule<IndexedObjectSomeValuesFrom> getInstance() {
		return INSTANCE_;
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void apply(IndexedObjectSomeValuesFrom premise, ContextPremises premises, ClassInferenceProducer producer) {
		DevTrace.log(LOG, "produceDecomposedExistentialLink: {}, {}, {}", producer, premises.getRoot(), premise);
		IndexedObjectSomeValuesFrom.Helper.produceDecomposedExistentialLink(producer, premises.getRoot(), premise);
	}

	@Override
	public boolean isTracingRule() {
		return true;
	}

	@Override
	public void accept(SubsumerDecompositionRuleVisitor<?> visitor, IndexedObjectSomeValuesFrom premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		DevTrace.log(LOG, "Visitor: {}", visitor);
		visitor.visit(this, premise, premises, producer);
	}

}
