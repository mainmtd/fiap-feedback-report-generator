package report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
@Named("reportGeneratorWorker")
public class ReportGeneratorWorker implements RequestHandler<JsonNode, Void> {

    @Inject
    ReportGeneratorService service;

    @Override
    public Void handleRequest(JsonNode input, Context context) {
        this.service.generateReport(input);

        return null;
    }

}

