package org.example.phoneduck.repository;

import org.example.phoneduck.model.ChatRoomModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatRoomModel, Long> {

    ChatRoomModel findByTitle(String title);

}
