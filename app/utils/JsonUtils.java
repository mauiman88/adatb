package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.twirl.api.Html;

import java.util.Collection;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ArrayNode newArrayNode() {
        return objectMapper.createArrayNode();
    }

    /**
     * Create a new ArrayNode from the given ObjectNodes
     * @param values
     * @return
     */
    public static ArrayNode newArrayNode(Collection<ObjectNode> values) {
        ArrayNode result = newArrayNode();
        for (ObjectNode value : values) {
            result.add(value);
        }
        return result;
    }

    /**
     * Create a JSON array as string from the given ObjectNodes
     * @param values
     * @return
     */
    public static String toJson(Collection<ObjectNode> values) {
        return Json.stringify(newArrayNode(values));
    }

    /**
     * Create a template-friendly Html which contains a JSON array from the given ObjectNodes
     * @param values
     * @return
     */
    public static Html toJsonHtml(Collection<ObjectNode> values) {
        return Html.apply(toJson(values));
    }

    /**
     * Same as the method above but it accepts Scala Seqs
     * @param values
     * @return
     */
    public static Html toJsonHtml(scala.collection.Seq<ObjectNode> values) {
        return Html.apply(toJson(scala.collection.JavaConversions.asJavaCollection(values)));
    }

}
