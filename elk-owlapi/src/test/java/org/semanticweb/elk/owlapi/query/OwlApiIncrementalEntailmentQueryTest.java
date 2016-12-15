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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owlapi.OwlApiIncrementalReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.BaseIncrementalQueryTest;
import org.semanticweb.elk.reasoner.query.EntailmentQueryTestOutput;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.MultiManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

@RunWith(PolySuite.class)
public class OwlApiIncrementalEntailmentQueryTest<O extends TestOutput> extends
		BaseIncrementalQueryTest<Collection<OWLAxiom>, OWLAxiom, EntailmentQueryTestOutput<OWLAxiom>> {

	public static final double INTERRUPTION_CHANCE = 0.01;

	public OwlApiIncrementalEntailmentQueryTest(
			final TestManifest<QueryTestInput<Collection<OWLAxiom>>> manifest) {
		super(manifest,
				new OwlApiIncrementalReasoningTestDelegate<EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>>(
						manifest, INTERRUPTION_CHANCE) {

					@Override
					public EntailmentQueryTestOutput<OWLAxiom> getExpectedOutput()
							throws Exception {
						final Map<OWLAxiom, Boolean> output = new HashMap<OWLAxiom, Boolean>();
						for (final OWLAxiom owlAxiom : manifest.getInput()
								.getQuery()) {
							output.put(owlAxiom,
									getStandardReasoner().isEntailed(owlAxiom));
						}
						return new EntailmentQueryTestOutput<OWLAxiom>(output);
					}

					@Override
					public EntailmentQueryTestOutput<OWLAxiom> getActualOutput()
							throws Exception {
						final Map<OWLAxiom, Boolean> output = new HashMap<OWLAxiom, Boolean>();
						for (final OWLAxiom owlAxiom : manifest.getInput()
								.getQuery()) {
							output.put(owlAxiom, getIncrementalReasoner()
									.isEntailed(owlAxiom));
						}
						return new EntailmentQueryTestOutput<OWLAxiom>(output);
					}

					@Override
					public Class<? extends Exception> getInterruptionExceptionClass() {
						return ReasonerInterruptedException.class;
					}

				});
	}

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION, BaseIncrementalQueryTest.class, "owl",
				"expected",
				new MultiManifestCreator<QueryTestInput<Collection<OWLAxiom>>, EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>>() {

					@Override
					public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<OWLAxiom>>, EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>>> createManifests(
							final URL input, final URL output)
							throws IOException {

						InputStream outputIS = null;
						try {
							outputIS = output.openStream();

							// don't need an expected output for these tests
							return OwlExpectedTestOutputLoader.load(outputIS)
									.getEntailmentManifests(input);

						} finally {
							IOUtils.closeQuietly(outputIS);
						}

					}

				});

	}

}
