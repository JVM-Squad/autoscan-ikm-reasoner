package org.semanticweb.elk.reasoner.indexing.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.util.collections.entryset.Entry;

/**
 * A non-atomic {@link CachedIndexedClassExpression}, that is, not an
 * {@link CachedIndexedClassEntity}
 *
 * @param <T>
 *            the type of the {@link CachedIndexedComplexClassExpression}
 */
public interface CachedIndexedComplexClassExpression<T extends CachedIndexedComplexClassExpression<T>>
		extends CachedIndexedClassExpression, IndexedComplexClassExpression,
		Entry<T, CachedIndexedComplexClassExpression<?>> {

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory
			extends
				CachedIndexedDataHasValue.Factory,
				CachedIndexedObjectComplementOf.Factory,
				CachedIndexedObjectHasSelf.Factory,
				CachedIndexedObjectIntersectionOf.Factory,
				CachedIndexedObjectSomeValuesFrom.Factory,
				CachedIndexedObjectUnionOf.Factory {
		
		// combined interface

	}
	
	/**
	 * A filter for mapping objects
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Filter
			extends
				CachedIndexedDataHasValue.Filter,
				CachedIndexedObjectComplementOf.Filter,
				CachedIndexedObjectHasSelf.Filter,
				CachedIndexedObjectIntersectionOf.Filter,
				CachedIndexedObjectSomeValuesFrom.Filter,
				CachedIndexedObjectUnionOf.Filter {

		// combined interface

	}
	
	CachedIndexedComplexClassExpression<?> accept(Filter filter);


}
