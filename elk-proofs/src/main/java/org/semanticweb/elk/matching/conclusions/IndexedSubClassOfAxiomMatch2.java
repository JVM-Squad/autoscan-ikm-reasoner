/*
 * This product is dual-licensed under Apache 2.0 License for two organizations due to forking.
 *
 * Copyright © 2023 Integrated Knowledge Management (support@ikm.dev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ======================================================================
 *
 * Copyright © 2011 - 2023 Department of Computer Science, University of Oxford
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.semanticweb.elk.matching.conclusions;



import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

public class IndexedSubClassOfAxiomMatch2
		extends
			AbstractIndexedAxiomMatch<IndexedSubClassOfAxiomMatch1> {

	private final ElkClassExpression subClassMatch_, superClassMatch_;

	IndexedSubClassOfAxiomMatch2(IndexedSubClassOfAxiomMatch1 parent,
			ElkClassExpression subClassMatch,
			ElkClassExpression superClassMatch) {
		super(parent);
		this.subClassMatch_ = subClassMatch;
		this.superClassMatch_ = superClassMatch;
	}

	public ElkClassExpression getSubClassMatch() {
		return subClassMatch_;
	}

	public ElkClassExpression getSuperClassMatch() {
		return superClassMatch_;
	}

	@Override
	public <O> O accept(IndexedAxiomMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		IndexedSubClassOfAxiomMatch2 getIndexedSubClassOfAxiomMatch2(
				IndexedSubClassOfAxiomMatch1 parent,
				ElkClassExpression subClassMatch,
				ElkClassExpression superClassMatch);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedSubClassOfAxiomMatch2 conclusionMatch);

	}

}
