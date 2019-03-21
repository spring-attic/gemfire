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

package org.springframework.cloud.stream.app.gemfire.sink;

import org.apache.geode.pdx.PdxInstance;

import org.springframework.cloud.stream.app.gemfire.JsonObjectTransformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.converter.MessageConversionException;

/**
 * @author David Turanski
 * @author Christian Tzolov
 **/
class GemfireSinkHandler {
	private final MessageHandler messageHandler;
	private final Boolean convertToJson;
	private final JsonObjectTransformer transformer = new JsonObjectTransformer();

	GemfireSinkHandler(MessageHandler messageHandler, Boolean convertToJson) {
		this.messageHandler = messageHandler;
		this.convertToJson = convertToJson;
	}

	public void handle(Message<?> message) {
		Message<?> transformedMessage = message;
		if (convertToJson) {
			Object payload = message.getPayload();

			PdxInstance transformedPayload;

			if (payload instanceof byte[]) {
				transformedPayload = transformer.toObject((byte[]) payload);
			}
			else if (payload instanceof String) {
				transformedPayload = transformer.toObject((String) payload);
			}
			else {
				throw new MessageConversionException(String.format(
						"Cannot convert object of type %s", payload.getClass()
								.getName()));
			}

			transformedMessage = MessageBuilder
					.fromMessage(message)
					.withPayload(transformedPayload)
					.build();

		}
		messageHandler.handleMessage(transformedMessage);
	}
}
