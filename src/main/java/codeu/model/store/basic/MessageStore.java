// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.model.store.basic;

import codeu.model.data.Message;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class MessageStore {

  /** Singleton instance of MessageStore. */
  private static MessageStore instance;

  /**
   * Returns the singleton instance of MessageStore that should be shared between all servlet
   * classes. Do not call this function from a test; use getTestInstance() instead.
   */
  public static MessageStore getInstance() {
    if (instance == null) {
      instance = new MessageStore(PersistentStorageAgent.getInstance());
    }
    return instance;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
   *
   * @param persistentStorageAgent a mock used for testing
   */
  public static MessageStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
    return new MessageStore(persistentStorageAgent);
  }

  /**
   * The PersistentStorageAgent responsible for loading Messages from and saving Messages to
   * Datastore.
   */
  private PersistentStorageAgent persistentStorageAgent;

  /** The in-memory list of Messages. */
  private List<Message> messages;

  /** The map of messages to their authors. */
  private HashMap<UUID, List<Message>> userMessages; 

  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private MessageStore(PersistentStorageAgent persistentStorageAgent) {
    this.persistentStorageAgent = persistentStorageAgent;
    messages = new ArrayList<>();
  }

  /** Access the current set of users known to the application. */
  public List<Message> getAllMessages() {
    return messages;
  }

  /** Maps each message to its author. */
  private void filterMessagesByAuthor(){
    userMessages = new HashMap<UUID, List<Message>>();
    for (Message message : messages){
      List<Message> msgs = userMessages.get(message.getAuthorId());
      if (msgs == null) {
          msgs = new ArrayList<>();
          userMessages.put(message.getAuthorId(), msgs);
      }
      msgs.add(message);
    }   
  }

  /** Returns the list of messages by a specific author. */
  public List<Message> getMessagesByAuthor(UUID authorId){
    return userMessages.get(authorId); 
  }

  /** Access the current set of Messages within the given Conversation. */
  public List<Message> getMessagesInConversation(UUID conversationId) {

    List<Message> messagesInConversation = new ArrayList<>();

    for (Message message : messages) {
      if (message.getConversationId() != null && message.getConversationId().equals(conversationId) && message.getParentMessageId() == null) {
        messagesInConversation.add(message);
      }
    }

    return messagesInConversation;
  }

  public Integer totalNumberOfMessages(){
    return messages.size();
  }
    
  public Message getMessage(UUID messageUUID) {
      for(Message message: messages){
          if(message.getId().equals(messageUUID))
             return message;
      }
      return null;
  }
    
  /** Add a new message to the current set of messages known to the application. */
  public void addMessage(Message message) {
    messages.add(message);
    persistentStorageAgent.writeThrough(message); 

    List<Message> msgs = userMessages.get(message.getAuthorId());
    if (msgs == null) {
        msgs = new ArrayList<>();
        userMessages.put(message.getAuthorId(), msgs);
    }
    msgs.add(message);    
  }
    
  /** Access the current set of Replies within the given Message. */
  public List<Message> getRepliesInMessage(UUID messageId) {
        
    List<Message> repliesInMsg = new ArrayList<>();
    
    for (Message reply : messages) {
        if (reply.getParentMessageId() != null && reply.getParentMessageId().equals(messageId)) {
            repliesInMsg.add(reply);
        }
    }
    
    return repliesInMsg;
  }

  /** Sets the List of Messages stored by this MessageStore. */
  public void setMessages(List<Message> messages) {
    this.messages = messages;
    filterMessagesByAuthor();
  }

}