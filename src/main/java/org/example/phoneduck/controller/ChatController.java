package org.example.phoneduck.controller;

import org.example.phoneduck.model.ChatRoomModel;
import org.example.phoneduck.model.MessageModel;
import org.example.phoneduck.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/phoneduck/channels")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    public ResponseEntity<List<String>> chatRoomList(){
        List<String> chatRooms = chatService.getAllRooms();
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    @GetMapping("/{title}")
    public ResponseEntity<?> getMessages(@PathVariable String title){
        List<MessageModel> messages = chatService.getAllMessages(title);
        if(messages != null && !messages.isEmpty()) {
            return new ResponseEntity<>(messages, HttpStatus.OK);
        }
        return new ResponseEntity<>("No messages found", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> addNewChatRoom(@RequestBody(required = false) ChatRoomModel chatRoomModel){
        if (chatRoomModel != null && chatRoomModel.getTitle() != null && !chatRoomModel.getTitle().trim().isEmpty()) {
            return chatService.createRoom(chatRoomModel);
        }
        return new ResponseEntity<>("Title cannot be empty", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Object> deleteChatRoom(@PathVariable String title){
        return chatService.deleteRoom(title);
    }

    @DeleteMapping("/{title}/messages/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable String title, @PathVariable int id){
            return chatService.deleteMessage(title, id);
    }

    @PatchMapping("/{title}")
    public ResponseEntity<?> updateChatRoom(@PathVariable String title, @RequestBody(required = false) ChatRoomModel newChatRoom) {
        if (newChatRoom != null) {
            return chatService.updateRoom(title, newChatRoom.getTitle());
        }
        return new ResponseEntity<>( "The request is empty and no change is being done to the chat room " + title,
                                    HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/{title}/messages/{id}")
    public ResponseEntity<Object> updateMessage(@PathVariable String title, @PathVariable int id, @RequestBody(required = false) MessageModel newMessage){
        return chatService.updateMessage(title, id, newMessage);
    }


    @PutMapping("/{title}") //fortsätt här med edge cases
    public ResponseEntity<?> createMessage(@PathVariable String title, @RequestBody(required = false) MessageModel message){
        ChatRoomModel chatRoom = chatService.findRoomByTitle(title);
        if(chatRoom != null) {
            if (message != null) {
                message.setChatRoom(chatRoom);
                return chatService.saveMessage(message);
            }
            return new ResponseEntity<>("The request was null", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>( "No chat room with the name " + title + " was found", HttpStatus.NOT_FOUND);
    }
}
