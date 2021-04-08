package cz.stanislavcapek.workattendancerest.v1.shiftplan.web;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * An instance of class {@code ShiftPlanTemplateLinks}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Component
public class ShiftPlanTemplateLinks {
    private static final String BASE_MAPPING = "/shift-plan-template";
    private static final Class<ShiftPlanTemplateController> CONTROLLER_CLASS = ShiftPlanTemplateController.class;

    static final String API_PATH = "/api/v1";
    static final String CURRENT_YEAR_TEMPLATE = BASE_MAPPING;
    static final String GIVEN_YEAR_TEMPLATE = BASE_MAPPING + "/{year}";

    static final LinkRelation CURRENT_YEAR_REL = LinkRelation.of("current-year-template");
    static final LinkRelation GIVEN_YEAR_REL = LinkRelation.of("given-year-template");


    Link getBaseMappingLink() {
        final UriComponents uriComponents = linkTo(CONTROLLER_CLASS)
                .toUriComponentsBuilder()
                .path(API_PATH)
                .path(CURRENT_YEAR_TEMPLATE)
                .build();

        return Link.of(uriComponents.toString(), CURRENT_YEAR_REL);

    }

    Link getGivenYearLink() {
        final UriComponents uriComponents = linkTo(CONTROLLER_CLASS)
                .toUriComponentsBuilder()
                .path(API_PATH)
                .path(GIVEN_YEAR_TEMPLATE)
                .build();

        return Link.of(uriComponents.toString(), GIVEN_YEAR_REL);

    }

}
