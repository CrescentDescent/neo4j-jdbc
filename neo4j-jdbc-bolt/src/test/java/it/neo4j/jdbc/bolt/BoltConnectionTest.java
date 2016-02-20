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
 * Created on 18/02/16
 */
package it.neo4j.jdbc.bolt;

import it.neo4j.jdbc.ResultSet;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * @author AgileLARUS
 * @since 3.0.0
 */
public class BoltConnectionTest {

	@Rule public ExpectedException expectedEx = ExpectedException.none();

	/*------------------------------*/
	/*           isClosed           */
	/*------------------------------*/
	@Ignore @Test public void isClosedShouldReturnFalse() throws SQLException {
		Connection connection = new BoltConnection();
		assertFalse(connection.isClosed());
	}

	@Ignore @Test public void isClosedShouldReturnTrue() throws SQLException {
		Connection connection = new BoltConnection();
		connection.close();
		assertTrue(connection.isClosed());
	}

	/*------------------------------*/
	/*             close            */
	/*------------------------------*/
	@Ignore @Test public void closeShouldCloseConnection() throws SQLException {
		Connection connection = new BoltConnection();
		assertFalse(connection.isClosed());
		connection.close();
		assertTrue(connection.isClosed());
	}

	/*------------------------------*/
	/*          isReadOnly          */
	/*------------------------------*/
	@Ignore @Test public void isReadOnlyShouldReturnFalse() throws SQLException {
		Connection connection = new BoltConnection();
		assertFalse(connection.isReadOnly());
	}

	@Ignore @Test public void isReadOnlyShouldReturnTrue() throws SQLException {
		Connection connection = new BoltConnection();
		connection.setReadOnly(true);
		assertTrue(connection.isReadOnly());
	}

	@Ignore @Test public void isReadOnlyShouldThrowExceptionWhenCalledOnAClosedConnection() throws SQLException {
		expectedEx.expect(SQLException.class);
		expectedEx.expectMessage("Connection already closed");

		Connection connection = new BoltConnection();
		connection.close();
		connection.isReadOnly();
	}

	/*------------------------------*/
	/*         setReadOnly          */
	/*------------------------------*/
	@Ignore @Test public void setReadOnlyShouldSetReadOnlyTrue() throws SQLException {
		Connection connection = new BoltConnection();
		assertFalse(connection.isReadOnly());
		connection.setReadOnly(true);
		assertTrue(connection.isReadOnly());
	}

	@Ignore @Test public void setReadOnlyShouldSetReadOnlyFalse() throws SQLException {
		Connection connection = new BoltConnection();
		assertFalse(connection.isReadOnly());
		connection.setReadOnly(false);
		assertFalse(connection.isReadOnly());
	}

	@Ignore @Test public void setReadOnlyShouldSetReadOnlyFalseAfterSetItTrue() throws SQLException {
		Connection connection = new BoltConnection();
		assertFalse(connection.isReadOnly());
		connection.setReadOnly(true);
		assertTrue(connection.isReadOnly());
		connection.setReadOnly(false);
		assertFalse(connection.isReadOnly());
	}

	@Ignore @Test public void setReadOnlyShouldThrowExceptionIfCalledOnAClosedConnection() throws SQLException {
		expectedEx.expect(SQLException.class);
		expectedEx.expectMessage("Connection already closed");

		Connection connection = new BoltConnection();
		connection.close();
		connection.setReadOnly(true);
	}

	/*------------------------------*/
	/*        createStatement       */
	/*------------------------------*/

	@Ignore @Test public void createStatementNoParamsShouldReturnNewStatement() throws SQLException {
		Connection connection = new BoltConnection();
		Statement statement = connection.createStatement();

		assertTrue(statement instanceof BoltStatement);
		assertEquals(connection.getHoldability(), statement.getResultSetHoldability());
		assertEquals(ResultSet.CONCUR_READ_ONLY, statement.getResultSetConcurrency());
		assertEquals(ResultSet.TYPE_FORWARD_ONLY, statement.getResultSetType());
	}

	@Ignore @Test public void createStatementNoParamsShouldThrowExceptionOnClosedConnection() throws SQLException {
		expectedEx.expect(SQLException.class);
		expectedEx.expectMessage("Connection already closed");
		Connection connection = new BoltConnection();
		connection.close();
		connection.createStatement();
	}

	@Ignore @Test public void createStatementTwoParamsShouldReturnNewStatement() throws SQLException {
		Connection connection = new BoltConnection();
		int[] types = { ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE };
		int[] concurrencies = { ResultSet.CONCUR_UPDATABLE, ResultSet.CONCUR_READ_ONLY };

		for (int type : types) {
			Statement statement = connection.createStatement(type, concurrencies[0]);
			assertEquals(type, statement.getResultSetType());
		}

		for (int concurrency : concurrencies) {
			Statement statement = connection.createStatement(types[0], concurrency);
			assertEquals(concurrency, statement.getResultSetConcurrency());
		}
	}

	@Ignore @Test public void createStatementTwoParamsShouldThrowExceptionOnClosedConnection() throws SQLException {
		expectedEx.expect(SQLException.class);
		expectedEx.expectMessage("Connection already closed");
		Connection connection = new BoltConnection();
		connection.close();
		connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

	//Must be managed only the types specified in the createStatementTwoParamsShouldReturnNewStatement test
	//This test doesn't cover all integers different from supported ones
	@Ignore @Test public void createStatementTwoParamsShouldThrowExceptionOnWrongParams() throws SQLException {
		Connection connection = new BoltConnection();

		int[] types = { ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE };
		int[] concurrencies = { ResultSet.CONCUR_UPDATABLE, ResultSet.CONCUR_READ_ONLY };

		for (int type : types) {
			try {
				connection.createStatement(concurrencies[0], type);
				fail();
			} catch (SQLFeatureNotSupportedException e) {

			} catch (Exception e) {
				fail();
			}
		}

		for (int concurrency : concurrencies) {
			try {
				connection.createStatement(concurrency, types[0]);
				fail();
			} catch (SQLFeatureNotSupportedException e) {

			} catch (Exception e) {
				fail();
			}
		}
	}

	@Ignore @Test public void createStatementThreeParamsShouldReturnNewStatement() throws SQLException {
		Connection connection = new BoltConnection();
		int[] types = { ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE };
		int[] concurrencies = { ResultSet.CONCUR_UPDATABLE, ResultSet.CONCUR_READ_ONLY };
		int[] holdabilities = { ResultSet.HOLD_CURSORS_OVER_COMMIT, ResultSet.CLOSE_CURSORS_AT_COMMIT };

		for (int type : types) {
			Statement statement = connection.createStatement(type, concurrencies[0], holdabilities[0]);
			assertEquals(type, statement.getResultSetType());
		}

		for (int concurrency : concurrencies) {
			Statement statement = connection.createStatement(types[0], concurrency, holdabilities[0]);
			assertEquals(concurrency, statement.getResultSetConcurrency());
		}

		for (int holdability : holdabilities) {
			Statement statement = connection.createStatement(types[0], concurrencies[0], holdability);
			assertEquals(holdability, statement.getResultSetHoldability());
		}
	}

	@Ignore @Test public void createStatementThreeParamsShouldThrowExceptionOnClosedConnection() throws SQLException {
		expectedEx.expect(SQLException.class);
		expectedEx.expectMessage("Connection already closed");
		Connection connection = new BoltConnection();
		connection.close();
		connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
	}

	//Must be managed only the types specified in the createStatementThreeParamsShouldReturnNewStatement test
	//This test doesn't cover all integers different from supported ones
	@Ignore @Test public void createStatementThreeParamsShouldThrowExceptionOnWrongParams() throws SQLException {
		Connection connection = new BoltConnection();

		int[] types = { ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE };
		int[] concurrencies = { ResultSet.CONCUR_UPDATABLE, ResultSet.CONCUR_READ_ONLY };
		int[] holdabilities = { ResultSet.HOLD_CURSORS_OVER_COMMIT, ResultSet.CLOSE_CURSORS_AT_COMMIT };

		for (int type : types) {
			try {
				connection.createStatement(concurrencies[0], holdabilities[0], type);
				fail();
			} catch (SQLFeatureNotSupportedException e) {

			} catch (Exception e) {
				fail();
			}
		}

		for (int concurrency : concurrencies) {
			try {
				connection.createStatement(concurrency, holdabilities[0], types[0]);
				fail();
			} catch (SQLFeatureNotSupportedException e) {

			} catch (Exception e) {
				fail();
			}
		}

		for (int holdability : holdabilities) {
			try {
				connection.createStatement(concurrencies[0], holdability, types[0]);
			}catch (SQLFeatureNotSupportedException e) {

			} catch (Exception e) {
				fail();
			}
		}
	}

	//TODO needs IT tests checking initialization succeeded
}