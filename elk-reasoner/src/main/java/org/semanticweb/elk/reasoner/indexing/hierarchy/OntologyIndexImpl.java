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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;

public class OntologyIndexImpl implements OntologyIndex {

	private IndexedClass indexedOwlThing, indexedOwlNothing;

	private final IndexedObjectCache objectCache_;
	private final IndexObjectConverter elkObjectIndexer_;
	private final ElkAxiomIndexerVisitor directAxiomInserter_,
			directAxiomDeleter_;

	private ChainableRule<Context> contextInitRules_ = null;

	public OntologyIndexImpl(IndexedObjectCache objectCache) {
		objectCache_ = objectCache;
		elkObjectIndexer_ = new IndexObjectConverter(objectCache, objectCache);

		indexPredefinedEntities();

		directAxiomInserter_ = new ElkAxiomIndexerVisitor(objectCache,
				getIndexedOwlNothing(), new DirectIndexUpdater(this), true);
		directAxiomDeleter_ = new ElkAxiomIndexerVisitor(objectCache,
				getIndexedOwlNothing(), new DirectIndexUpdater(this), false);
	}

	public OntologyIndexImpl() {
		this(new IndexedObjectCache());
	}

	@Override
	public void clear() {
		objectCache_.clear();
		indexPredefinedEntities();
	}

	@Override
	public IndexedObjectCache getIndexedObjectCache() {
		return this.objectCache_;
	}

	/**
	 * Process predefine declarations of OWL ontologies
	 */
	private void indexPredefinedEntities() {
		// index predefined entities
		// TODO: what to do if someone tries to delete them?
		ElkAxiomIndexerVisitor tmpIndexer = new ElkAxiomIndexerVisitor(
				objectCache_, null, new DirectIndexUpdater(this), true);

		this.indexedOwlThing = tmpIndexer
				.indexClassDeclaration(PredefinedElkClass.OWL_THING);
		this.indexedOwlNothing = tmpIndexer
				.indexClassDeclaration(PredefinedElkClass.OWL_NOTHING);
	}

	@Override
	public LinkRule<Context> getContextInitRuleHead() {
		return contextInitRules_;
	}

	@Override
	public Chain<ChainableRule<Context>> getContextInitRuleChain() {
		return new AbstractChain<ChainableRule<Context>>() {

			@Override
			public ChainableRule<Context> next() {
				return contextInitRules_;
			}

			@Override
			public void setNext(ChainableRule<Context> tail) {
				contextInitRules_ = tail;
			}
		};
	}

	@Override
	public IndexedClassExpression getIndexed(ElkClassExpression representative) {
		IndexedClassExpression result = representative
				.accept(elkObjectIndexer_);
		if (result.occurs())
			return result;
		else
			return null;
	}

	@Override
	public IndexedPropertyChain getIndexed(
			ElkSubObjectPropertyExpression elkSubObjectPropertyExpression) {
		IndexedPropertyChain result = elkSubObjectPropertyExpression
				.accept(elkObjectIndexer_);
		if (result.occurs())
			return result;
		else
			return null;
	}

	@Override
	public Collection<IndexedClassExpression> getIndexedClassExpressions() {
		return objectCache_.indexedClassExpressionLookup;
	}

	@Override
	public Collection<IndexedClass> getIndexedClasses() {
		return new AbstractCollection<IndexedClass>() {
			@Override
			public Iterator<IndexedClass> iterator() {
				return Operations.filter(getIndexedClassExpressions(),
						IndexedClass.class).iterator();
			}

			@Override
			public int size() {
				return objectCache_.indexedClassCount;
			}
		};
	}

	@Override
	public Collection<IndexedIndividual> getIndexedIndividuals() {
		return new AbstractCollection<IndexedIndividual>() {

			@Override
			public Iterator<IndexedIndividual> iterator() {
				return Operations.filter(getIndexedClassExpressions(),
						IndexedIndividual.class).iterator();
			}

			@Override
			public int size() {
				return objectCache_.indexedIndividualCount;
			}

		};
	}

	@Override
	public Collection<IndexedPropertyChain> getIndexedPropertyChains() {
		return objectCache_.indexedPropertyChainLookup;
	}

	@Override
	public Collection<IndexedObjectProperty> getIndexedObjectProperties() {
		return new AbstractCollection<IndexedObjectProperty>() {

			@Override
			public Iterator<IndexedObjectProperty> iterator() {
				return Operations.filter(getIndexedPropertyChains(),
						IndexedObjectProperty.class).iterator();
			}

			@Override
			public int size() {
				return objectCache_.indexedObjectPropertyCount;
			}
		};
	}

	@Override
	public ElkAxiomProcessor getAxiomInserter() {
		return directAxiomInserter_;
	}

	@Override
	public ElkAxiomProcessor getAxiomDeleter() {
		return directAxiomDeleter_;
	}

	@Override
	public IndexedClass getIndexedOwlThing() {
		return indexedOwlThing;
	}

	@Override
	public IndexedClass getIndexedOwlNothing() {
		return indexedOwlNothing;
	}
}