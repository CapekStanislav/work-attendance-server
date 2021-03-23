package cz.stanislavcapek.workattendancerest.v1.holiday;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Instance třídy {@code HolidaysDao}
 *
 * @author Stanislav Čapek
 */
@Service
public final class HolidayLoaderFromUrlService {

    public HolidayLoaderFromUrlService() {
    }

    /**
     * Načte svátky z url
     *
     * @param year rok pro který zjišťujeme svátky
     */
    public List<Holiday> getHolidaysFromUrl(int year) throws IOException {
        final Document document;

        final String url = String.format("http://svatky.centrum.cz/svatky/statni-svatky/%d/", year);
        document = Jsoup.connect(url).get();
        final Elements elements = document.select("#list-names");
        final Elements tds = elements.select("td");

        List<Holiday> holidayList = new ArrayList<>();
        for (int i = 0; i < tds.size() - 1; i += 2) {
            String s = tds.get(i).text().replaceAll("\\.", "");
            final String[] split = s.split(" ");
            int day = Integer.parseInt(split[0]);
            int month = Integer.parseInt(split[1]);
            holidayList.add(
                    new Holiday(
                            LocalDate.of(year, month, day),
                            tds.get(i + 1).text()
                    )
            );
        }

        return new ArrayList<>(
                holidayList.stream()
                        .collect(
                                Collectors.toMap(
                                        Holiday::getDate, holiday -> holiday,
                                        (h1, h2) -> new Holiday(
                                                h1.getDate(), String.format("%s / %s", h1.getName(), h2.getName())
                                        )
                                )
                        )
                        .values()
        );
    }
}
