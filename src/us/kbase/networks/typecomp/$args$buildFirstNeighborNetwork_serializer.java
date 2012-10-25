package us.kbase.networks.client;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;


public class $args$buildFirstNeighborNetwork_serializer extends JsonSerializer<$args$buildFirstNeighborNetwork>
{
    public void serialize($args$buildFirstNeighborNetwork value, JsonGenerator jgen, SerializerProvider provider)
	throws IOException, JsonProcessingException
    {
	jgen.writeStartArray();
	jgen.writeObject(value.ParameterList);
	jgen.writeEndArray();
    }
}
