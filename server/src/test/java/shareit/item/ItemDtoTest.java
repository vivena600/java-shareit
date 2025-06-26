package shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = ShareItServer.class)
public class ItemDtoTest {
    private final LocalDateTime testTime = LocalDateTime.of(2025, 07, 25, 00, 00, 00);

    private final JacksonTester<ItemDto> json;
    private final JacksonTester<ItemWithCommentDto> jsonCom;

    @Test
    void testItemDto() throws IOException {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(1L)
                .requestId(1L)
                .build();

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testItemWithCommentDto() throws IOException {
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("authorName")
                .created(testTime)
                .build();

        ItemWithCommentDto dto = ItemWithCommentDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(1L)
                .comments(List.of(comment))
                .build();

        JsonContent<ItemWithCommentDto> result = jsonCom.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);

        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo("authorName");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo("2025-07-25T00:00:00");
    }
}
