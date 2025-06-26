package shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = ShareItServer.class)
public class BookingDtoTest {
    private final JacksonTester<BookingDto> json;

    private final LocalDateTime testTime = LocalDateTime.of(2025, 07, 25, 00, 00, 00);

    @Test
    void testBookingDto() throws IOException {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(testTime.minusDays(1))
                .end(testTime.plusDays(1))
                .itemId(1L)
                .booker(1L)
                .status(BookingStatus.WAITING.toString())
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-07-24T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2025-07-26T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(BookingStatus.WAITING.toString());
    }
}
