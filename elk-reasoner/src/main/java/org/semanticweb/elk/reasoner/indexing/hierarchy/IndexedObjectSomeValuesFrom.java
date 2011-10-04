/*
 * #%L
 * elk-reasoner
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.ArrayList;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectSomeValuesFromVisitable;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectSomeValuesFromVisitor;

/**
 * Represents all occurrences of an ElkObjectSomeValuesFrom in an ontology.
 * 
 * @author Frantisek Simancik
 * 
 */
public class IndexedObjectSomeValuesFrom extends IndexedClassExpression
		implements IndexedObjectSomeValuesFromVisitable {

	protected final IndexedObjectProperty relation;

	protected final IndexedClassExpression filler;

	IndexedObjectSomeValuesFrom(IndexedObjectProperty indexedObjectProperty,
			IndexedClassExpression filler) {
		super(new ArrayList<ElkClassExpression>(1));
		this.relation = indexedObjectProperty;
		this.filler = filler;
	}

	/**
	 * @return The indexed object property comprising this ObjectSomeValuesFrom.
	 */
	public IndexedObjectProperty getRelation() {
		return relation;
	}

	/**
	 * @return The indexed class expression comprising this
	 *         ObjectSomeValuesFrom.
	 */
	public IndexedClassExpression getFiller() {
		return filler;
	}

	public <O> O accept(IndexedObjectSomeValuesFromVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectSomeValuesFromVisitor<O>) visitor);
	}

	@Override
	protected void updateOccurrenceNumbers(int increment, int positiveIncrement,
			int negativeIncrement) {

		if (this.negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this conjunction
			this.filler.addNegExistential(this);
		}

		this.occurrenceNo += increment;
		this.positiveOccurrenceNo += positiveIncrement;
		this.negativeOccurrenceNo += negativeIncrement;

		if (this.negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			this.filler.removeNegExistential(this);
		}

		this.filler.updateOccurrenceNumbers(increment, positiveIncrement,
				negativeIncrement);
	}

}