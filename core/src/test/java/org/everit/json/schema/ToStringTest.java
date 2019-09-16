package org.everit.json.schema;

import static org.everit.json.schema.JSONMatcher.sameJsonAs;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;

import org.everit.json.schema.internal.JSONPrinter;
import org.json.JSONObject;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ToStringTest {

    static class CustomSchema extends Schema {

        static class CustomSchemaBuilder extends Schema.Builder<CustomSchema>  {

            @Override public CustomSchema build() {
                return new CustomSchema(this);
            }
        }


        /**
         * Constructor.
         *
         * @param builder
         *         the builder containing the optional title, description and id attributes of the schema
         */
        protected CustomSchema(Builder<?> builder) {
            super(builder);
        }

        @Override void accept(Visitor visitor) {
            visitor.visitSchema(this);
        }

        @Override void describePropertiesTo(JSONPrinter writer) {
            writer.key("custom").value("schema");
        }

        @Override public String toString() {
            StringWriter w = new StringWriter();
            JSONPrinter printer = new JSONPrinter(w);
            printer.object();
            ToStringVisitor visitor = new ToStringVisitor(printer);
            visitor.visit(this);
            printer.endObject();
            return w.getBuffer().toString();
        }
    }

    private static final ResourceLoader LOADER = new ResourceLoader("/org/everit/jsonvalidator/tostring/");

    @Test
    public void testCustomSchemaWithDescribePropertiesTo() {
        String actual = new CustomSchema(new CustomSchema.CustomSchemaBuilder().description("descr-custom")).toString();
        assertThat(new JSONObject(actual), sameJsonAs(LOADER.readObj("custom-schema.json")));
    }

    @Test
    public void testBooleanSchema() {
        BooleanSchema subject = BooleanSchema.builder()
                .id("bool-id")
                .title("bool-title")
                .description("bool-description")
                .unprocessedProperties(ImmutableMap.of("$schema", "https://json-schema.org/draft-07/schema"))
                .build();
        assertThat(new JSONObject(subject.toString()), sameJsonAs(LOADER.readObj("boolean-schema.json")));
    }

    @Test
    public void testArraySchema() {
        ArraySchema subject = ArraySchema.builder()
                .uniqueItems(true)
                .minItems(5).maxItems(10)
                .allItemSchema(BooleanSchema.INSTANCE).build();
        String actual = subject.toString();
        assertThat(new JSONObject(actual), sameJsonAs(LOADER.readObj("arrayschema-list.json")));
    }


}