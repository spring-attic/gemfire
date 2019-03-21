/*
 * Copyright (c) 2016-2018 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License") ;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.gemfire;

import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.integration.transformer.MessageTransformationException;

/**
 * @author David Turanski
 * @author Christian Tzolov
 *
 */
public class JsonObjectTransformer {

	public PdxInstance toObject(String json) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		}
		catch (JSONException e) {
			throw new MessageTransformationException(e.getMessage());
		}
		return JSONFormatter.fromJSON(jsonObject.toString());
	}

	public PdxInstance toObject(byte[] json) {
		return toObject(new String(json));
	}

	public String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof PdxInstance) {
			String json = JSONFormatter.toJSON((PdxInstance) obj);
			// de-pretty
			return json.replaceAll("\\r\\n\\s*", "").replaceAll("\\n\\s*", "")
					.replaceAll("\\s*:\\s*", ":").trim();
		}
		return obj.toString();
	}
}
