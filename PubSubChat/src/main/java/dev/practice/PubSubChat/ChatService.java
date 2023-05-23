package dev.practice.PubSubChat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class ChatService implements MessageListener {

    private final RedisMessageListenerContainer redisMessageListenerContainer;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("Message: " + message.toString());
    }

    /**
     * 채팀방 입장
     * - ListenerContainer 에 Listener(this) 등록 -> 메시지가 push 되면 onMessage() 메서드가 Redis 에 의해 호출된다.
     * - 사용자의 입력을 기다리고 종료 조건 검사
     */
    public void enterChatRoom(String chatRoomName) {
        redisMessageListenerContainer.addMessageListener(this, new ChannelTopic(chatRoomName));

        System.out.printf("Chat Room(%s) entered..%n", chatRoomName);

        Scanner in = new Scanner(System.in);

        while (in.hasNextLine()) {

            String line = in.nextLine();

            if(line.equals("q")) {
                System.out.println("Exit..");
                break;
            }
        }
    }
}
