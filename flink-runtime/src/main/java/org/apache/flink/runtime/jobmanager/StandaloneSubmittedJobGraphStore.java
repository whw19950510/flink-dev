/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.jobmanager;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * {@link SubmittedJobGraph} instances for JobManagers running in {@link HighAvailabilityMode#NONE}.
 *
 * <p>All operations are NoOps, because {@link JobGraph} instances cannot be recovered in this
 * recovery mode.
 */
public class StandaloneSubmittedJobGraphStore implements SubmittedJobGraphStore {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	private final List<SubmittedJobGraph> jobGraphs;

	public StandaloneSubmittedJobGraphStore() {
		this.jobGraphs = new ArrayList<>();
	}

	@Override
	public void start(SubmittedJobGraphListener jobGraphListener) throws Exception {
		// Nothing to do
	}

	@Override
	public void stop() {
		// Nothing to do
	}

	@Override
	public void putJobGraph(SubmittedJobGraph jobGraph) {
		this.jobGraphs.add(jobGraph);
		log.info("job graph added");
		log.info("new job id {}", jobGraph.getJobId());
		log.info("TOTAL JOB: " + this.jobGraphs.size());
		// Nothing to do
	}

	@Override
	public void removeJobGraph(JobID jobId) {
		for (SubmittedJobGraph jobGraph: this.jobGraphs) {
			if (jobGraph.getJobId().equals(jobId)) {
				this.jobGraphs.remove(jobGraph);
				return;
			}
		}
	}

	@Override
	public void releaseJobGraph(JobID jobId) {
		// nothing to do
	}

	@Override
	public Collection<JobID> getJobIds() {
		log.info("TOTAL JOBs " + this.jobGraphs.size());
		Set<JobID> result = new HashSet<>();
		for (SubmittedJobGraph j: this.jobGraphs) {
			result.add(j.getJobId());
		}
		return result;
	}

	@Override
	public SubmittedJobGraph recoverJobGraph(JobID jobId) {
		for (SubmittedJobGraph jobGraph: this.jobGraphs) {
			if (jobGraph.getJobId().equals(jobId)) {
				return jobGraph;
			}
		}
		return null;
	}

	public boolean isShareable(SubmittedJobGraph newJobGraph) {
		for (SubmittedJobGraph jobGraph: this.jobGraphs) {
			if (jobGraph.getJobGraph().isShareable(newJobGraph.getJobGraph())) {
				return true;
			}
		}
		return false;
	}
}
