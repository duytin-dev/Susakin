package com.susakin.app.config;

import com.susakin.app.entity.Lesson;
import com.susakin.app.entity.LessonProgress;
import com.susakin.app.entity.Topic;
import com.susakin.app.entity.User;
import com.susakin.app.entity.UserVocabulary;
import com.susakin.app.entity.Vocabulary;
import com.susakin.app.repository.LessonProgressRepository;
import com.susakin.app.repository.LessonRepository;
import com.susakin.app.repository.TopicRepository;
import com.susakin.app.repository.UserRepository;
import com.susakin.app.repository.UserVocabularyRepository;
import com.susakin.app.repository.VocabularyRepository;
import com.susakin.app.utils.enums.LessonType;
import com.susakin.app.utils.enums.ProgressStatus;
import com.susakin.app.utils.enums.VocabStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private static final int VOCABULARIES_PER_TOPIC = 200;

    private final TopicRepository topicRepository;
    private final VocabularyRepository vocabularyRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final UserVocabularyRepository userVocabularyRepository;
    private final PasswordEncoder passwordEncoder;
    private final VocabularySeedLoader vocabularySeedLoader;

    @Override
    @Transactional
    public void run(String... args) {
        if (topicRepository.count() == 0) {
            log.info("Đang seed dữ liệu mẫu...");
            List<Topic> topics = seedTopics();
            seedVocabularies(topics);
            List<Lesson> lessons = seedLessons(topics);
            List<User> users = seedUsers();
            seedLessonProgress(users.get(0), lessons);
            seedUserVocabularies(users.get(0), topics);
            log.info("Seed dữ liệu mẫu hoàn tất.");
            return;
        }

        ensureVocabulariesPerTopic();
    }

    private void ensureVocabulariesPerTopic() {
        List<Topic> topics = topicRepository.findAll();
        boolean updated = false;

        for (Topic topic : topics) {
            long currentCount = vocabularyRepository.countByTopicId(topic.getId());
            if (currentCount >= VOCABULARIES_PER_TOPIC) {
                continue;
            }

            log.info("Chủ đề '{}' có {} từ, bổ sung lên {} từ...",
                    topic.getName(), currentCount, VOCABULARIES_PER_TOPIC);

            List<Vocabulary> existing = vocabularyRepository.findByTopicIdOrderByOrderIndexAsc(topic.getId());
            Set<String> existingWords = new HashSet<>();
            int maxOrderIndex = 0;
            for (Vocabulary vocabulary : existing) {
                existingWords.add(vocabulary.getWord().toLowerCase());
                maxOrderIndex = Math.max(maxOrderIndex, vocabulary.getOrderIndex());
            }

            List<VocabularySeedLoader.WordEntry> seedWords = vocabularySeedLoader.loadForTopic(topic.getName());
            List<Vocabulary> toAdd = new ArrayList<>();
            int orderIndex = maxOrderIndex;

            for (VocabularySeedLoader.WordEntry entry : seedWords) {
                if (existingWords.size() + toAdd.size() >= VOCABULARIES_PER_TOPIC) {
                    break;
                }
                if (existingWords.contains(entry.word().toLowerCase())) {
                    continue;
                }
                orderIndex++;
                toAdd.add(createVocabulary(topic, entry.word(), entry.meaningVi(), orderIndex));
                existingWords.add(entry.word().toLowerCase());
            }

            if (!toAdd.isEmpty()) {
                vocabularyRepository.saveAll(toAdd);
                updated = true;
                log.info("Đã thêm {} từ cho chủ đề '{}'", toAdd.size(), topic.getName());
            }
        }

        if (!updated) {
            log.info("Mỗi chủ đề đã có đủ {} từ vựng.", VOCABULARIES_PER_TOPIC);
        }
    }

    private List<Topic> seedTopics() {
        List<Topic> topics = List.of(
                createTopic("Động vật", "https://cdn.example.com/topics/animals.png", 1),
                createTopic("Màu sắc", "https://cdn.example.com/topics/colors.png", 2),
                createTopic("Gia đình", "https://cdn.example.com/topics/family.png", 3),
                createTopic("Thức ăn", "https://cdn.example.com/topics/food.png", 4)
        );
        return topicRepository.saveAll(topics);
    }

    private Topic createTopic(String name, String thumbnailUrl, int orderIndex) {
        Topic topic = new Topic();
        topic.setName(name);
        topic.setThumbnailUrl(thumbnailUrl);
        topic.setOrderIndex(orderIndex);
        return topic;
    }

    private void seedVocabularies(List<Topic> topics) {
        List<Vocabulary> vocabularies = new ArrayList<>();

        for (Topic topic : topics) {
            List<VocabularySeedLoader.WordEntry> words = vocabularySeedLoader.loadForTopic(topic.getName());
            int orderIndex = 1;
            for (VocabularySeedLoader.WordEntry entry : words) {
                if (orderIndex > VOCABULARIES_PER_TOPIC) {
                    break;
                }
                vocabularies.add(createVocabulary(topic, entry.word(), entry.meaningVi(), orderIndex++));
            }
        }

        vocabularyRepository.saveAll(vocabularies);
        log.info("Đã seed {} từ vựng ({} từ/chủ đề).",
                vocabularies.size(), VOCABULARIES_PER_TOPIC);
    }

    private Vocabulary createVocabulary(Topic topic, String word, String meaningVi, int orderIndex) {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setTopic(topic);
        vocabulary.setWord(word);
        vocabulary.setMeaningVi(meaningVi);
        vocabulary.setImageUrl("https://cdn.example.com/vocab/" + word.replace(" ", "-") + ".png");
        vocabulary.setAudioUrl("https://cdn.example.com/audio/" + word.replace(" ", "-") + ".mp3");
        vocabulary.setOrderIndex(orderIndex);
        return vocabulary;
    }

    private List<Lesson> seedLessons(List<Topic> topics) {
        List<Lesson> lessons = new ArrayList<>();

        for (Topic topic : topics) {
            lessons.add(createLesson(topic, "Flashcard - " + topic.getName(), LessonType.FLASHCARD, 1));
            lessons.add(createLesson(topic, "Matching - " + topic.getName(), LessonType.MATCHING, 2));
            lessons.add(createLesson(topic, "Quiz - " + topic.getName(), LessonType.QUIZ, 3));
        }

        return lessonRepository.saveAll(lessons);
    }

    private Lesson createLesson(Topic topic, String title, LessonType type, int orderIndex) {
        Lesson lesson = new Lesson();
        lesson.setTopic(topic);
        lesson.setTitle(title);
        lesson.setType(type);
        lesson.setOrderIndex(orderIndex);
        return lesson;
    }

    private List<User> seedUsers() {
        User demo = new User();
        demo.setEmail("demo@example.com");
        demo.setName("Tin");
        demo.setPasswordHash(passwordEncoder.encode("123456"));
        demo.setGrade(3);

        User student = new User();
        student.setEmail("student@example.com");
        student.setName("Lan");
        student.setPasswordHash(passwordEncoder.encode("123456"));
        student.setGrade(2);

        return userRepository.saveAll(List.of(demo, student));
    }

    private void seedLessonProgress(User user, List<Lesson> lessons) {
        LessonProgress progress1 = new LessonProgress();
        progress1.setUser(user);
        progress1.setLesson(lessons.get(0));
        progress1.setStatus(ProgressStatus.COMPLETED);
        progress1.setScore(90);
        progress1.setCompletedAt(LocalDateTime.now().minusDays(1));

        LessonProgress progress2 = new LessonProgress();
        progress2.setUser(user);
        progress2.setLesson(lessons.get(1));
        progress2.setStatus(ProgressStatus.COMPLETED);
        progress2.setScore(85);
        progress2.setCompletedAt(LocalDateTime.now().minusHours(5));

        LessonProgress progress3 = new LessonProgress();
        progress3.setUser(user);
        progress3.setLesson(lessons.get(2));
        progress3.setStatus(ProgressStatus.IN_PROGRESS);
        progress3.setScore(40);

        lessonProgressRepository.saveAll(List.of(progress1, progress2, progress3));
    }

    private void seedUserVocabularies(User user, List<Topic> topics) {
        List<Vocabulary> vocabularies = vocabularyRepository.findByTopicIdOrderByOrderIndexAsc(topics.get(0).getId());

        UserVocabulary uv1 = new UserVocabulary();
        uv1.setUser(user);
        uv1.setVocabulary(vocabularies.get(0));
        uv1.setStatus(VocabStatus.MASTERED);
        uv1.setLearnedAt(LocalDateTime.now().minusDays(2));

        UserVocabulary uv2 = new UserVocabulary();
        uv2.setUser(user);
        uv2.setVocabulary(vocabularies.get(1));
        uv2.setStatus(VocabStatus.LEARNING);
        uv2.setLearnedAt(LocalDateTime.now().minusDays(1));

        UserVocabulary uv3 = new UserVocabulary();
        uv3.setUser(user);
        uv3.setVocabulary(vocabularies.get(2));
        uv3.setStatus(VocabStatus.NEW);
        uv3.setLearnedAt(LocalDateTime.now());

        userVocabularyRepository.saveAll(List.of(uv1, uv2, uv3));
    }
}
