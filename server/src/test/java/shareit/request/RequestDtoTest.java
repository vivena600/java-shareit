package shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.request.dto.FullRequestDto;
import ru.practicum.shareit.request.dto.RequestAddDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = ShareItServer.class)
public class RequestDtoTest {
    private final JacksonTester<RequestDto> json;
    private final JacksonTester<RequestAddDto> jsonAdd;
    private final JacksonTester<FullRequestDto> jsonFull;

    private final LocalDateTime testTime = LocalDateTime.of(2025, 07, 25, 00, 00, 00);

    @Test
    void testRequestDto() throws IOException {
        RequestDto dto = RequestDto.builder()
                .id(1L)
                .userId(1L)
                .description("test")
                .created(testTime)
                .build();

        JsonContent<RequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-07-25T00:00:00");
    }

    @Test
    void testRequestAddDto() throws IOException {
        RequestAddDto dto = new RequestAddDto("test");

        JsonContent<RequestAddDto> result = jsonAdd.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
    }

    @Test
    void testFullRequestDto() throws IOException {
        ShortItemDto item = ShortItemDto.builder()
                .id(1L)
                .userId(1L)
                .description("test")
                .name("test")
                .build();

        FullRequestDto dto = FullRequestDto.builder()
                .id(1L)
                .userId(1L)
                .description("test")
                .created(testTime)
                .items(List.of(item))
                .build();

        JsonContent<FullRequestDto> result = jsonFull.write(dto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-07-25T00:00:00");

        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].userId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("test");
    }
}
