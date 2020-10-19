package com.twilio.oai;

import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.languages.GoClientCodegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwilioTerraformGenerator extends GoClientCodegen {

    public TwilioTerraformGenerator() {
        super();

        embeddedTemplateDir = templateDir = getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> postProcessOperationsWithModels(final Map<String, Object> objs, final List<Object> allModels) {
        final Map<String, Object> results = super.postProcessOperationsWithModels(objs, allModels);

        final Map<String, Map<String, Object>> resources = new HashMap<>();

        final Map<String, Object> ops = (Map<String, Object>) results.get("operations");
        final ArrayList<CodegenOperation> opList = (ArrayList<CodegenOperation>) ops.get("operation");

        opList.removeIf(co -> "list".equals(co.vendorExtensions.get("x-path-type")));

        // iterate over the operation and perhaps modify something
        for (final CodegenOperation co : opList) {
            co.vendorExtensions.put("x-is-create-operation", co.nickname.endsWith("Create"));
            co.vendorExtensions.put("x-is-read-operation", co.nickname.endsWith("Read"));
            co.vendorExtensions.put("x-is-update-operation", co.nickname.endsWith("Update"));
            co.vendorExtensions.put("x-is-delete-operation", co.nickname.endsWith("Delete"));

            this.addNameInSnakeCase(co.allParams);
            this.addNameInSnakeCase(co.optionalParams);

            final String resourceName = co.path
                .replaceFirst("\\/[^/]+", "") // Drop the version
                .replaceAll("\\/\\{.+?\\}", "") // Drop every path parameter
                .replace(".json", "") // Drop the JSON extension
                .replace("/", ""); // Drop the path separators

            final Map<String, Object> resource = resources.computeIfAbsent(resourceName, k -> new HashMap<>());
            final Map<String, Object> resourceOperations = (Map<String, Object>) resource.computeIfAbsent("operations", k -> new HashMap<>());
            final ArrayList<CodegenOperation> resourceOperationList = (ArrayList<CodegenOperation>) resourceOperations.computeIfAbsent("operation", k -> new ArrayList<>());

            resource.put("name", resourceName);
            resourceOperationList.add(co);

            if (co.nickname.endsWith("Create")) {
                resource.put("schema", co.allParams);
            }
        }

        results.put("resources", resources.values());

        return results;
    }

    private void addNameInSnakeCase(final List<CodegenParameter> params) {
        params.forEach(p -> p.vendorExtensions.put("x-name-in-snake-case", this.toSnakeCase(p.baseName)));
    }

    private String toSnakeCase(final String string) {
        return string.replaceAll("([a-z\\d])([A-Z])", "$1_$2").toLowerCase();
    }

    @Override
    public Map<String, String> createMapping(final String key, final String value) {
        // Optional dependency not needed.
        if (value.equals("github.com/antihax/optional")) {
            return new HashMap<>();
        }

        return super.createMapping(key, value);
    }

    /**
     * Configures a friendly name for the generator.  This will be used by the generator
     * to select the library with the -g flag.
     *
     * @return the friendly name for the generator
     */
    @Override
    public String getName() {
        return "terraform-provider-twilio";
    }

    /**
     * Returns human-friendly help for the generator.  Provide the consumer with help
     * tips, parameters here
     *
     * @return A string value for the help message
     */
    @Override
    public String getHelp() {
        return "Generates a Terraform provider (beta).";
    }
}