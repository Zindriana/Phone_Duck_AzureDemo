package org.example.phoneduck.service;

import org.example.phoneduck.model.ChatRoomModel;
import org.example.phoneduck.model.MessageModel;
import org.example.phoneduck.repository.ChatRepository;
import org.example.phoneduck.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;

    public List<String> getAllRooms() {
        List<ChatRoomModel> chatRooms = chatRepository.findAll();
        List<String> titles = new ArrayList<>();
        for(ChatRoomModel chatroom: chatRooms){
            titles.add(chatroom.getTitle());
        }
        return titles;
    }

    public ChatRoomModel findRoomByTitle(String title){
        return chatRepository.findByTitle(title);
    }

    public ResponseEntity<Object> createRoom(ChatRoomModel chatRoomModel){
        if (chatRepository.findByTitle(chatRoomModel.getTitle()) == null) {
            chatRepository.save(chatRoomModel);
            String title = chatRoomModel.getTitle();
            return new ResponseEntity<>("The channel " + title + " was successfully created",
                                        HttpStatus.CREATED);
        }
        return new ResponseEntity<>("A chat room must have a name", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> deleteRoom(String title){
        ChatRoomModel room = chatRepository.findByTitle(title);
        if(room == null){
            return new ResponseEntity<>("No chat room with the name " + title + " was found",
                                        HttpStatus.BAD_REQUEST);
        }
        if(room.getId()!=1){ //General chat (id 1) is a permanent room
            chatRepository.delete(room);
            return new ResponseEntity<>("The channel " + title + " was successfully deleted",
                                        HttpStatus.OK);
        }
        return new ResponseEntity<>("You cannot delete the General chat",
                                    HttpStatus.NOT_ACCEPTABLE); //General chat (id 1) is a permanent room
    }

    public ResponseEntity<?> deleteMessage(String title, int id){
        ChatRoomModel room = chatRepository.findByTitle(title);
        MessageModel message = messageRepository.findById(id);
        if (room != null) {
            if(message != null) {
                if (message.getChatRoom().equals(room)) {
                    messageRepository.delete(message);
                    return new ResponseEntity<>("The message was successfully deleted", HttpStatus.OK);
                }
                return new ResponseEntity<>("The message does not belong to the chat room " + title,
                        HttpStatus.BAD_REQUEST);
            }
                return new ResponseEntity<>("Did not find the chosen message-ID " + id,
                                        HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("The room " + title + " was not found",
                                    HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> updateRoom(String title, String newTitle){
        ChatRoomModel room = chatRepository.findByTitle(title);
        if (newTitle != null && !newTitle.trim().isEmpty() && room != null && room.getId()!=1) {
            room.setTitle(newTitle);
            chatRepository.save(room);
            return new ResponseEntity<>("The chat room " + title + " has successfully been updated to " + newTitle,
                                        HttpStatus.OK);
        }
        return new ResponseEntity<>("No chat room with the name " + title + " was found, or the request was empty",
                                    HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> updateMessage(String title, int id, MessageModel newMessage){
        MessageModel message = messageRepository.findById(id);
        ChatRoomModel room = chatRepository.findByTitle(title);
        newMessage.setId((long) id);
        newMessage.setChatRoom(room);
        if (room != null && message != null){
            if (message.getChatRoom().equals(room)) {
                message = newMessage;
                messageRepository.save(message);
                return new ResponseEntity<>("The message has successfully been updated",
                                            HttpStatus.OK);
            }
            return new ResponseEntity<>("The message does not belong to chat room " + title,
                                        HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("You must choose a chat room name and a message-ID",
                                        HttpStatus.BAD_REQUEST);
    }

    public List<MessageModel> getAllMessages(String title) {
        ChatRoomModel room = chatRepository.findByTitle(title);
        if(room != null) {
            return messageRepository.findAllByChatRoom(room);
        }
        return null;
    }

    public ResponseEntity<Object> saveMessage(MessageModel message){
        if(message.getMessage() != null) {
            messageRepository.save(message);
            return new ResponseEntity<>("The message was successfully created", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("The message is null",HttpStatus.BAD_REQUEST);
    }
}