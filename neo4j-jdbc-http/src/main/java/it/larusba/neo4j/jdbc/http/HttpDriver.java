/**
 * Copyright (c) 2016 LARUS Business Automation [http://www.larus-ba.it]
 * <p>
 * This file is part of the "LARUS Integration Framework for Neo4j".
 * <p>
 * The "LARUS Integration Framework for Neo4j" is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created on 15/4/2016
 */
package it.larusba.neo4j.jdbc.http;

import it.larusba.neo4j.jdbc.Driver;
import it.larusba.neo4j.jdbc.InstanceFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC Driver class for the HTTP connector.
 */
public class HttpDriver extends Driver {

	public final static String JDBC_HTTP_PREFIX = "http";

	/**
	 * Default constructor.
	 */
	public HttpDriver() throws SQLException {
		super(JDBC_HTTP_PREFIX);
	}

	@Override public Connection connect(String url, Properties params) throws SQLException {
		Connection connection = null;
		try {
			if (acceptsURL(url)) {
				URL neo4jUrl = new URL(url.replace("jdbc:", ""));
				Properties info = parseUrlProperties(url, params);
				String host = neo4jUrl.getHost();
				int port = 7474;
				if (neo4jUrl.getPort() > 0) {
					port = neo4jUrl.getPort();
				}
				connection = InstanceFactory.debug(HttpConnection.class, new HttpConnection(host, port, info), HttpConnection.hasDebug(info));
			}
			else {
				throw new SQLException("JDBC url is not bad");
			}
		} catch (MalformedURLException e) {
			throw new SQLException(e);
		}

		return connection;
	}

}
