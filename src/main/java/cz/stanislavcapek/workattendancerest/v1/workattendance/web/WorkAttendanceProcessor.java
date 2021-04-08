package cz.stanislavcapek.workattendancerest.v1.workattendance.web;

import cz.stanislavcapek.workattendancerest.v1.workattendance.WorkAttendance;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.hateoas.server.TypedEntityLinks;
import org.springframework.stereotype.Service;

/**
 * An instance of class {@code WorkAttendanceProcessor}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
@Service
@AllArgsConstructor
public class WorkAttendanceProcessor implements RepresentationModelProcessor<EntityModel<WorkAttendance>> {

    private EntityLinks entityLinks;
    private WorkAttendanceUtilService service;

    @Override
    public EntityModel<WorkAttendance> process(EntityModel<WorkAttendance> model) {

        final WorkAttendance workAttendance = model.getContent();

        service.setValidHoursInMonth(workAttendance);

        addLinks(model);

        return model;
    }

    private void addLinks(EntityModel<WorkAttendance> model) {
        final WorkAttendance workAttendance = model.getContent();


        final Link workTimeSumLink = getLink(workAttendance, "work-time-sum");

        final Link premiumPaymentsSumLink = getLink(workAttendance, "premium-payments-sum");

        final Link lockLink = getLink(workAttendance, "lock");

        final Link typesLink = getLink(workAttendance, "types");

        final Link generateLink = getLink(workAttendance, "generate");

        model.add(workTimeSumLink, premiumPaymentsSumLink, lockLink, typesLink,generateLink);
    }

    private Link getLink(WorkAttendance workAttendance, String text) {
        final TypedEntityLinks<WorkAttendance> typedEntityLinks =
                entityLinks.forType(WorkAttendance::getWorkAttendanceId);
        return typedEntityLinks
                .linkForItemResource(workAttendance)
                .slash(text)
                .withRel(text);
    }
}
