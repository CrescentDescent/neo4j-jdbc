package it.larusba.neo4j.jdbc.http.driver;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.larusba.neo4j.jdbc.http.test.Neo4jHttpUnitTest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public class Neo4jResponseTest extends Neo4jHttpUnitTest {

    @Test
    public void hasErrorShoudlReturnFalse() throws SQLException {
        Neo4jResponse response = generateNeo4jResponse(Boolean.FALSE);
        Assert.assertFalse(response.hasErrors());
    }

    @Test
    public void hasErrorShoudlReturnTrue() throws SQLException {
        Neo4jResponse response = generateNeo4jResponse(Boolean.TRUE);
        Assert.assertTrue(response.hasErrors());
    }

    @Test
    public void displayErrorShouldSucceed() throws SQLException {
        // Without errors
        Neo4jResponse response = generateNeo4jResponse(Boolean.FALSE);
        Assert.assertEquals(0, response.displayErrors().length());

        // With errors
        response = generateNeo4jResponse(Boolean.TRUE);
        Assert.assertTrue(response.displayErrors().length() > 0);
    }

    /**
     * Create a response object.
     *
     * @return
     */
    private Neo4jResponse generateNeo4jResponse(Boolean withErrors) throws SQLException {
        String body = "";
        if (withErrors) {
            body = "{\n" +
                    "  \"results\" : [ ],\n" +
                    "  \"errors\" : [ {\n" +
                    "    \"code\" : \"Neo.ClientError.Statement.InvalidSyntax\",\n" +
                    "    \"message\" : \"Invalid input 'T': expected <init> (line 1, column 1 (offset: 0))\\n\\\"This is not a valid Cypher Statement.\\\"\\n ^\"\n" +
                    "  } ]\n" +
                    "}";
        } else {
            body = "{\n" +
                    "  \"results\" : [ {\n" +
                    "    \"columns\" : [ \"id(n)\" ],\n" +
                    "    \"data\" : [ {\n" +
                    "      \"row\" : [ 74 ],\n" +
                    "      \"meta\" : [ null ]\n" +
                    "    } ]\n" +
                    "  }, {\n" +
                    "    \"columns\" : [ \"n\" ],\n" +
                    "    \"data\" : [ {\n" +
                    "      \"row\" : [ {\n" +
                    "        \"name\" : \"My Node\"\n" +
                    "      } ],\n" +
                    "      \"meta\" : [ {\n" +
                    "        \"id\" : 75,\n" +
                    "        \"type\" : \"node\",\n" +
                    "        \"deleted\" : false\n" +
                    "      } ]\n" +
                    "    } ]\n" +
                    "  } ],\n" +
                    "  \"errors\" : [ ]\n" +
                    "}";
        }
        Neo4jResponse mock = new Neo4jResponse(mockHttpResponse(200, body), new ObjectMapper());
        return mock;
    }

    /**
     * Mock an HttpResponse.
     */
    private HttpResponse mockHttpResponse(int code, String body) {
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), code, ""));
        response.setStatusCode(code);
        try {
            response.setEntity(new StringEntity(body));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        return response;
    }

}