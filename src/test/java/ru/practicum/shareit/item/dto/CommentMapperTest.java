package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentMapperTest {

    private Comment comment;
    private CommentDto commentDto;
    private Item item;
    private User user;
    private static final String nowDate = "2023-08-24T01:29:22";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    public void setUp() {
        user = mock(User.class);
        when(user.getName()).thenReturn("John Doe");

        item = mock(Item.class);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("This is a comment.");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.parse(nowDate, formatter));

        commentDto = new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    @Test
    public void testToCommentDto() {
        CommentDto mappedCommentDto = CommentMapper.toCommentDto(comment);

        assertEquals(commentDto.getId(), mappedCommentDto.getId());
        assertEquals(commentDto.getText(), mappedCommentDto.getText());
        assertEquals(commentDto.getAuthorName(), mappedCommentDto.getAuthorName());
        assertEquals(commentDto.getCreated(), mappedCommentDto.getCreated());
    }

    @Test
    public void testToComment() {
        CommentDto testCommentDto = new CommentDto(
                2L,
                "Another comment.",
                "Jane Doe",
                LocalDateTime.now()
        );

        Comment mappedComment = CommentMapper.toComment(testCommentDto, item, user);

        assertEquals(testCommentDto.getId(), mappedComment.getId());
        assertEquals(testCommentDto.getText(), mappedComment.getText());
        assertEquals(item, mappedComment.getItem());
        assertEquals(user, mappedComment.getAuthor());
        assertEquals(testCommentDto.getCreated(), mappedComment.getCreated());
    }
}