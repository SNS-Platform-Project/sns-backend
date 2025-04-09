package com.example.snsbackend.domain.chat;

import com.example.snsbackend.dto.ChatRoomRequest;
import com.example.snsbackend.dto.NoOffsetPage;
import com.example.snsbackend.dto.PageParam;
import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.model.ChatRoom;
import com.example.snsbackend.model.Profile;
import com.example.snsbackend.repository.ChatRoomRepository;
import com.example.snsbackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MongoTemplate mongoTemplate;

    public ChatRoom createChatRoom(ChatRoomRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        long count = mongoTemplate.count(new Query(Criteria.where("id").is(request.getUserIds())), Profile.class);

        if (count != request.getUserIds().size()) {
            throw new IllegalArgumentException("존재하지 않는 유저가 포함되어 있습니다.");
        }
        request.getUserIds().add(userId);

        return chatRoomRepository.save(new ChatRoom(request));
    }

    public boolean isUserInRoom(String userId, String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(NoSuchElementException::new);
        return chatRoom.getParticipants().contains(userId);
    }

    public NoOffsetPage<ChatRoom> getChatRooms(PageParam param) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        Query query = new Query(Criteria.where("participants").in(userId));
        query.with(Sort.by(Sort.Direction.DESC, "lastMessage.messageId"));
        if (param.getLastId() != null) {
            query.addCriteria(Criteria.where("lastMessage.sentAt").lt(new ObjectId(param.getLastId()).getDate()));
        }
        query.limit(param.getSize());
        List<ChatRoom> rooms = mongoTemplate.find(query, ChatRoom.class);

        return new NoOffsetPage<>(rooms, rooms.getLast().getLastMessageId(), param.getSize());
    }
}
