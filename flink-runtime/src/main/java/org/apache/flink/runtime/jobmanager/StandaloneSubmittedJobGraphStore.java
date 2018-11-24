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

import org.apache.commons.logging.LogFactory;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link SubmittedJobGraph} instances for JobManagers running in {@link HighAvailabilityMode#NONE}.
 *
 * <p>All operations are NoOps, because {@link JobGraph} instances cannot be recovered in this
 * recovery mode.
 */
public class StandaloneSubmittedJobGraphStore implements SubmittedJobGraphStore {
	private List<SubmittedJobGraph> submittedJobGraphList;

	public StandaloneSubmittedJobGraphStore() {
		submittedJobGraphList = new ArrayList<>();
	}

	protected final Logger log = LoggerFactory.getLogger(getClass());


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
		// Nothing to do
		for(SubmittedJobGraph cur: submittedJobGraphList) {
			if(cur.getJobId().equals(jobGraph.getJobId()))
				return;
		}
		submittedJobGraphList.add(jobGraph);
	}

	@Override
	public void removeJobGraph(JobID jobId) {
		// Nothing to do
		int candidate = Integer.MAX_VALUE;
		for(int i = 0; i <  submittedJobGraphList.size(); i++) {
			if(jobId.equals(submittedJobGraphList.get(i).getJobId())) {
				submittedJobGraphList.remove(candidate);
				log.warn("remove one jobGraph from list {}", candidate);
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
		List<JobID> result = new ArrayList<>();
		for(SubmittedJobGraph cur : submittedJobGraphList) {
			result.add(cur.getJobId());
		}
		return result;
	}

	@Override
	public SubmittedJobGraph recoverJobGraph(JobID jobId) {
		for(SubmittedJobGraph cur: submittedJobGraphList) {
			if(jobId.equals(cur.getJobId()))
				return cur;
		}
		return null;
	}
}
