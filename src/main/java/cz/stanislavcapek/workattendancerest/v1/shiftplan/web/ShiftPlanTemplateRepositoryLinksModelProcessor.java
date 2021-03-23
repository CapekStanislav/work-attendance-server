package cz.stanislavcapek.workattendancerest.v1.shiftplan.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Service;

/**
 * An instance of class {@code CustomResourceProcessor}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ShiftPlanTemplateRepositoryLinksModelProcessor
        implements RepresentationModelProcessor<RepositoryLinksResource> {

    private final ShiftPlanTemplateLinks links;

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource model) {

        System.out.println("CustomResourceProcessor.process");

        final Link baseMappingLink = links.getBaseMappingLink();
        final Link givenYearLink = links.getGivenYearLink();

        System.out.println("baseMappingLink = " + baseMappingLink);
        System.out.println("givenYearLink = " + givenYearLink);

        model.add(baseMappingLink, givenYearLink);

        return model;
    }
}
