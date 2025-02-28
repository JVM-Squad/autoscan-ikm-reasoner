/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.query;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.semanticweb.elk.owlapi.TestOWLManager;
import org.semanticweb.elk.reasoner.query.EmptyTestOutput;
import org.semanticweb.elk.reasoner.query.QueryTestManifest;
import org.semanticweb.elk.reasoner.query.SatisfiabilityTestOutput;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;

/**
 * @author Peter Skocovsky
 * @see #load(InputStream)
 */
public class OwlExpectedTestOutputLoader {

	/**
	 * Loads an expected output of a class query test.
	 * <p>
	 * The expected output should be stored in an ontology that is loaded from
	 * the supplied argument. The complex classes in this ontology will be
	 * recognized as query classes. Classes equivalent to each query class
	 * should be in single equivalence axiom with the query class. Direct
	 * super(sub)-classes of each query class should be partitioned into sets of
	 * equivalent classes, each of which should be expressed by a single
	 * equivalence axiom if there is more than one class in the set. Whether
	 * such a set contains super- or sub-classes of the query class should be
	 * expressed by a single subclass axiom of the query class with one of the
	 * related classes. Analogously for individuals: grouped by same individuals
	 * axioms and related by class assertion axioms.
	 * 
	 * @param expectedOutput
	 *            contains the ontology that encode the expected output
	 * @return an object providing the leaded exected test output
	 */
	public static OwlExpectedTestOutputLoader load(
			final InputStream expectedOutput) {

		final OWLOntologyManager manager = TestOWLManager
				.createOWLOntologyManager();
		try {

			final OWLOntology expectedOnt = manager
					.loadOntologyFromOntologyDocument(expectedOutput);

			final Set<OWLClassExpression> complex = new HashSet<OWLClassExpression>();
			final Map<OWLClassExpression, OWLClassNode> equivalent = new HashMap<OWLClassExpression, OWLClassNode>();
			final MutableMultimap<OWLClassExpression, OWLClass> superClasses = new UnifiedSetMultimap<OWLClassExpression, OWLClass>();
			final MutableMultimap<OWLClassExpression, OWLClass> subClasses = new UnifiedSetMultimap<OWLClassExpression, OWLClass>();
			final Map<OWLIndividual, OWLNamedIndividualNode> same = new HashMap<OWLIndividual, OWLNamedIndividualNode>();
			final MutableMultimap<OWLClassExpression, OWLNamedIndividual> instances = new UnifiedSetMultimap<OWLClassExpression, OWLNamedIndividual>();

			for (final OWLAxiom axiom : expectedOnt.getAxioms()) {
				axiom.accept(new FailingOwlAxiomVisitor() {

					@Override
					public void visit(final OWLEquivalentClassesAxiom axiom) {
						final Set<OWLClass> owlClasses = new HashSet<OWLClass>();
						for (final OWLClassExpression ce : axiom
								.getClassExpressions()) {
							if (ce instanceof OWLClass) {
								owlClasses.add((OWLClass) ce);
							} else {
								complex.add(ce);
							}
						}
						final OWLClassNode node = new OWLClassNode(owlClasses);
						for (final OWLClassExpression ce : axiom
								.getClassExpressions()) {
							equivalent.put(ce, node);
						}
					}

					@Override
					public void visit(final OWLSubClassOfAxiom axiom) {
						if (axiom.getSubClass() instanceof OWLClass) {
							subClasses.put(axiom.getSuperClass(),
									(OWLClass) axiom.getSubClass());
						} else {
							complex.add(axiom.getSubClass());
						}
						if (axiom.getSuperClass() instanceof OWLClass) {
							superClasses.put(axiom.getSubClass(),
									(OWLClass) axiom.getSuperClass());
						} else {
							complex.add(axiom.getSuperClass());
						}
					}

					@Override
					public void visit(final OWLSameIndividualAxiom axiom) {
						final Set<OWLNamedIndividual> individuals = new HashSet<OWLNamedIndividual>();
						for (final OWLIndividual i : axiom.getIndividuals()) {
							if (i instanceof OWLNamedIndividual) {
								individuals.add((OWLNamedIndividual) i);
							}
						}
						final OWLNamedIndividualNode node = new OWLNamedIndividualNode(
								individuals);
						for (final OWLIndividual i : axiom.getIndividuals()) {
							same.put(i, node);
						}
					}

					@Override
					public void visit(final OWLClassAssertionAxiom axiom) {
						if (axiom
								.getIndividual() instanceof OWLNamedIndividual) {
							instances.put(axiom.getClassExpression(),
									(OWLNamedIndividual) axiom.getIndividual());
						}
						if (!(axiom.getClassExpression() instanceof OWLClass)) {
							complex.add(axiom.getClassExpression());
						}
					}

				});
			}

			return new OwlExpectedTestOutputLoader(complex, equivalent,
					superClasses, subClasses, same, instances);

		} catch (final OWLOntologyCreationException e) {
			throw new IllegalArgumentException(e);
		}

	}

	final Set<OWLClassExpression> queryClasses_;
	final Map<OWLClassExpression, OWLClassNode> equivalent_;
	final MutableMultimap<OWLClassExpression, OWLClass> superClasses_;
	final MutableMultimap<OWLClassExpression, OWLClass> subClasses_;
	final Map<OWLIndividual, OWLNamedIndividualNode> same_;
	final MutableMultimap<OWLClassExpression, OWLNamedIndividual> instances_;

	private OwlExpectedTestOutputLoader(
			final Set<OWLClassExpression> queryClasses,
			final Map<OWLClassExpression, OWLClassNode> equivalent,
			final MutableMultimap<OWLClassExpression, OWLClass> superClasses,
			final MutableMultimap<OWLClassExpression, OWLClass> subClasses,
			final Map<OWLIndividual, OWLNamedIndividualNode> same,
			final MutableMultimap<OWLClassExpression, OWLNamedIndividual> instances) {
		this.queryClasses_ = queryClasses;
		this.equivalent_ = equivalent;
		this.superClasses_ = superClasses;
		this.subClasses_ = subClasses;
		this.same_ = same;
		this.instances_ = instances;
	}

	public Collection<QueryTestManifest<OWLClassExpression, EmptyTestOutput>> getNoOutputManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<OWLClassExpression, EmptyTestOutput>> result = new ArrayList<>(
				queryClasses_.size());

		for (final OWLClassExpression queryClass : queryClasses_) {
			result.add(
					new QueryTestManifest<OWLClassExpression, EmptyTestOutput>(
							name, input, queryClass, null));
		}

		return result;
	}

	public Collection<QueryTestManifest<OWLClassExpression, SatisfiabilityTestOutput>> getClassSatisfiabilityManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<OWLClassExpression, SatisfiabilityTestOutput>> result = new ArrayList<>(
				queryClasses_.size());

		for (final OWLClassExpression queryClass : queryClasses_) {
			final OWLClassNode node = equivalent_.get(queryClass);
			// If the query class is equivalent to bottom, node is NOT null!
			result.add(
					new QueryTestManifest<OWLClassExpression, SatisfiabilityTestOutput>(
							name + " isSatisfiable", input, queryClass,
							new OwlClassExpressionSatisfiabilityTestOutput(
									node == null || !node.isBottomNode())));
		}

		return result;
	}

	public Collection<QueryTestManifest<OWLClassExpression, OwlEquivalentClassesTestOutput>> getEquivalentClassesManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<OWLClassExpression, OwlEquivalentClassesTestOutput>> result = new ArrayList<>(
				queryClasses_.size());

		for (final OWLClassExpression queryClass : queryClasses_) {
			final OWLClassNode node = equivalent_.get(queryClass);
			result.add(
					new QueryTestManifest<OWLClassExpression, OwlEquivalentClassesTestOutput>(
							name + " getEquivalentClasses", input, queryClass,
							new OwlEquivalentClassesTestOutput(
									node == null ? new OWLClassNode() : node)));
		}

		return result;
	}

	public Collection<QueryTestManifest<OWLClassExpression, OwlDirectSuperClassesTestOutput>> getDirectSuperClassesManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<OWLClassExpression, OwlDirectSuperClassesTestOutput>> result = new ArrayList<>(
				queryClasses_.size());

		for (final OWLClassExpression queryClass : queryClasses_) {

			final Collection<OWLClassNode> superNodes = Operations.map(
					superClasses_.get(queryClass),
					new Operations.Transformation<OWLClass, OWLClassNode>() {
						@Override
						public OWLClassNode transform(final OWLClass cls) {
							final OWLClassNode result = equivalent_.get(cls);
							if (result != null) {
								return result;
							}
							// else
							return new OWLClassNode(cls);
						}
					});

			result.add(
					new QueryTestManifest<OWLClassExpression, OwlDirectSuperClassesTestOutput>(
							name + " getDirectSuperClasses", input, queryClass,
							new OwlDirectSuperClassesTestOutput(queryClass,
									new HashSet<Node<OWLClass>>(superNodes))));
		}

		return result;
	}

	public Collection<QueryTestManifest<OWLClassExpression, OwlDirectSubClassesTestOutput>> getDirectSubClassManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<OWLClassExpression, OwlDirectSubClassesTestOutput>> result = new ArrayList<>(
				queryClasses_.size());

		for (final OWLClassExpression queryClass : queryClasses_) {

			final Collection<OWLClassNode> subNodes = Operations.map(
					subClasses_.get(queryClass),
					new Operations.Transformation<OWLClass, OWLClassNode>() {
						@Override
						public OWLClassNode transform(final OWLClass cls) {
							final OWLClassNode result = equivalent_.get(cls);
							if (result != null) {
								return result;
							}
							// else
							return new OWLClassNode(cls);
						}
					});

			result.add(
					new QueryTestManifest<OWLClassExpression, OwlDirectSubClassesTestOutput>(
							name + " getDirectSubClasses", input, queryClass,
							new OwlDirectSubClassesTestOutput(queryClass,
									new HashSet<Node<OWLClass>>(subNodes))));
		}

		return result;
	}

	public Collection<QueryTestManifest<OWLClassExpression, OwlDirectInstancesTestOutput>> getDirectInstancesManifests(
			final String name, final URL input) {

		final List<QueryTestManifest<OWLClassExpression, OwlDirectInstancesTestOutput>> result = new ArrayList<>(
				queryClasses_.size());

		for (final OWLClassExpression queryClass : queryClasses_) {

			final Collection<OWLNamedIndividualNode> instances = Operations.map(
					instances_.get(queryClass),
					new Operations.Transformation<OWLNamedIndividual, OWLNamedIndividualNode>() {
						@Override
						public OWLNamedIndividualNode transform(
								final OWLNamedIndividual ind) {
							final OWLNamedIndividualNode result = same_
									.get(ind);
							if (result != null) {
								return result;
							}
							// else
							return new OWLNamedIndividualNode(ind);
						}
					});

			result.add(
					new QueryTestManifest<OWLClassExpression, OwlDirectInstancesTestOutput>(
							name + " getDirectInstances", input, queryClass,
							new OwlDirectInstancesTestOutput(queryClass,
									new HashSet<Node<OWLNamedIndividual>>(
											instances))));
		}

		return result;
	}

	public Collection<? extends QueryTestManifest<OWLAxiom, OwlEntailmentQueryTestOutput>> getEntailmentManifests(
			final String name, final URL input) {

		final OWLDataFactory owlFactory = TestOWLManager.getOWLDataFactory();

		final List<OWLAxiom> query = new ArrayList<>();
		final Map<OWLAxiom, Boolean> output = new HashMap<OWLAxiom, Boolean>();

		for (final OWLClassExpression queryClass : queryClasses_) {

			// Equivalent to queryClass.

			final OWLClassNode equivalent = equivalent_.get(queryClass);
			if (equivalent != null && equivalent.getSize() != 0) {

				final Set<OWLClassExpression> equivalentClasses = new HashSet<OWLClassExpression>(
						1 + equivalent.getSize());
				equivalentClasses.add(queryClass);
				equivalentClasses.addAll(equivalent.getEntities());

				final OWLAxiom axiom = owlFactory
						.getOWLEquivalentClassesAxiom(equivalentClasses);
				query.add(axiom);
				output.put(axiom, true);

			}

			// Superclasses of queryClass.

			final Collection<OWLClass> superClasses = superClasses_
					.get(queryClass);
			if (superClasses != null && !superClasses.isEmpty()) {
				final OWLClass superClass = superClasses.iterator().next();

				OWLAxiom axiom = owlFactory.getOWLSubClassOfAxiom(queryClass,
						superClass);
				query.add(axiom);
				output.put(axiom, true);

				axiom = owlFactory.getOWLSubClassOfAxiom(superClass,
						queryClass);
				query.add(axiom);
				output.put(axiom, false);

				if (equivalent != null && equivalent.getSize() != 0) {
					final Set<OWLClassExpression> equivalentClasses = new HashSet<OWLClassExpression>(
							2 + equivalent.getSize());
					equivalentClasses.add(queryClass);
					equivalentClasses.add(superClass);
					equivalentClasses.addAll(equivalent.getEntities());
					axiom = owlFactory
							.getOWLEquivalentClassesAxiom(equivalentClasses);
					query.add(axiom);
					output.put(axiom, false);
				}

			}

			// Subclasses of queryClass.

			final Collection<OWLClass> subClasses = subClasses_.get(queryClass);
			if (subClasses != null && !subClasses.isEmpty()) {
				final OWLClass subClass = subClasses.iterator().next();

				OWLAxiom axiom = owlFactory.getOWLSubClassOfAxiom(subClass,
						queryClass);
				query.add(axiom);
				output.put(axiom, true);

				axiom = owlFactory.getOWLSubClassOfAxiom(queryClass, subClass);
				query.add(axiom);
				output.put(axiom, false);

				if (equivalent != null && equivalent.getSize() != 0) {
					final Set<OWLClassExpression> equivalentClasses = new HashSet<OWLClassExpression>(
							2 + equivalent.getSize());
					equivalentClasses.add(queryClass);
					equivalentClasses.add(subClass);
					equivalentClasses.addAll(equivalent.getEntities());
					axiom = owlFactory
							.getOWLEquivalentClassesAxiom(equivalentClasses);
					query.add(axiom);
					output.put(axiom, false);
				}

			}

			// Instances of queryClass.

			final Collection<OWLNamedIndividual> instances = instances_
					.get(queryClass);
			if (instances != null && !instances.isEmpty()) {
				final OWLNamedIndividual instance = instances.iterator().next();

				OWLAxiom axiom = owlFactory
						.getOWLClassAssertionAxiom(queryClass, instance);
				query.add(axiom);
				output.put(axiom, true);

				// Find individual that is not an instance.
				OWLNamedIndividual notInstance = null;
				for (final OWLClassExpression type : instances_.keySet()) {
					for (final OWLNamedIndividual individual : instances_
							.get(type)) {
						if (!instances.contains(individual)) {
							notInstance = individual;
							break;
						}
					}
					if (notInstance != null) {
						break;
					}
				}
				if (notInstance != null) {

					axiom = owlFactory.getOWLClassAssertionAxiom(queryClass,
							notInstance);
					query.add(axiom);
					output.put(axiom, false);

				}

			}

		}

		if (query.isEmpty()) {
			return Collections.emptySet();
		}
		// else

		// OWL API interface can query only one axiom at once.
		final Collection<QueryTestManifest<OWLAxiom, OwlEntailmentQueryTestOutput>> manifests = new ArrayList<>(
				query.size());
		for (final OWLAxiom axiom : query) {
			boolean axiomProved = output.get(axiom);
			manifests.add(
					new QueryTestManifest<OWLAxiom, OwlEntailmentQueryTestOutput>(
							name + " isEntailed", input, axiom,
							new OwlEntailmentQueryTestOutput(axiom, axiomProved,
									!axiomProved)));
		}
		return manifests;
	}

}
