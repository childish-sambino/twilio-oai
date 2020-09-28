package com.twilio.oai;

import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.languages.GoClientCodegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwilioGoGenerator extends GoClientCodegen {

    public TwilioGoGenerator() {
        super();

        embeddedTemplateDir = templateDir = "twilio-go";
    }

    @Override
    public void processOpts() {
        super.processOpts();

        additionalProperties.put(CodegenConstants.IS_GO_SUBMODULE, true);

        supportingFiles.clear();
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
        supportingFiles.add(new SupportingFile("configuration.mustache", "", "configuration.go"));
        supportingFiles.add(new SupportingFile("response.mustache", "", "response.go"));
        supportingFiles.add(new SupportingFile("go.mod.mustache", "", "go.mod"));
        supportingFiles.add(new SupportingFile("go.sum", "", "go.sum"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> postProcessOperationsWithModels(Map<String, Object> objs, List<Object> allModels) {
        Map<String, Object> results = super.postProcessOperationsWithModels(objs, allModels);

        Map<String, Object> ops = (Map<String, Object>) results.get("operations");
        ArrayList<CodegenOperation> opList = (ArrayList<CodegenOperation>) ops.get("operation");

        // iterate over the operation and perhaps modify something
        for (CodegenOperation co : opList) {
            System.out.println(co);
        }

        return results;
    }

    @Override
    public void postProcessParameter(CodegenParameter parameter) {
        // Make sure required non-path params get into the options block.
        parameter.required = parameter.isPathParam;
    }

    @Override
    public Map<String, String> createMapping(String key, String value) {
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
        return "twilio-go";
    }

    /**
     * Returns human-friendly help for the generator.  Provide the consumer with help
     * tips, parameters here
     *
     * @return A string value for the help message
     */
    @Override
    public String getHelp() {
        return "Generates a Go client library (beta).";
    }
}
