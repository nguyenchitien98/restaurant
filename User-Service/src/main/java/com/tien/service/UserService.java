package com.tien.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.tien.dto.response.UserResponse;
import com.tien.mapper.UserMapper;
import com.tien.model.User;
import com.tien.model.elasticsearch.UserDocument;
import com.tien.repository.UserRepository;
import com.tien.repository.elasticseach.UserSearchRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    private static final int BATCH_SIZE = 1000;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // Dùng redis để lưu cache , Cache theo key "{page}-{size}", ví dụ "0-50" sẽ cache trang 0 với size 50.
    // Cache usersPage tự động hết hạn sau 2 phút. Có config redis trong config
    @Cacheable(value = "usersPage", key = "#page + '-' + #size")
    public Page<User> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
        return userRepository.findAll(pageable);
    }

    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 2. Cập nhật user, đồng thời cập nhật lại cache
    @CachePut(value = "users", key = "#user.userId")
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // 3. Xoá user và xoá luôn cache tương ứng
    @CacheEvict(value = "users",key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // 4. (Tuỳ chọn) Xoá toàn bộ cache
    @CacheEvict(value = "users", allEntries = true)
    public void clearAllUserCache() {
        // Dọn sạch cache user: allEntries = true
    }

    public List<UserDocument> searchByUsername(String userName) {
        return userSearchRepository.findByUsernameContainingIgnoreCase(userName);
    }

    // Import file CSV
    @Async
    public void importUsersAsync(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .build();

            List<User> userBatch = new ArrayList<>();
            String[] line;
            int processedCount = 0;
            int batchNumber = 1;

            long start = System.currentTimeMillis();

            while ((line = csvReader.readNext()) != null) {
                User user = new User(); // Mỗi line là một mảng String[] đại diện cho 1 dòng CSV.
                user.setUsername(line[0]);
                user.setPassword(line[1]);
                user.setEmail(line[2]);
                user.setRole(line[3]);
                userBatch.add(user);  // Thêm vào userBatch.
                processedCount++;

                if (userBatch.size() >= BATCH_SIZE) {
                    userRepository.saveAll(userBatch);
                    userRepository.flush(); // buộc ghi xuống DB ngay
                    userBatch.clear(); // xóa batch để ghi batch tiếp theo

                    logger.info("Batch {} saved. Total processed: {} users", batchNumber++, processedCount);
                }
            }

            if (!userBatch.isEmpty()) {
                userRepository.saveAll(userBatch);
                userRepository.flush();
                logger.info("Final batch saved. Total processed: {} users", processedCount);
            }

            long end = System.currentTimeMillis();
            logger.info("Import CSV hoàn tất! Tổng cộng: {} users. Thời gian: {} ms", processedCount, (end - start));
        } catch (Exception e) {
            logger.error(" Lỗi khi import CSV: ", e);
        }
    }

//    public UserResponse getUserById(Long userId) {
//        User user = userRepository.findByUserId(userId);
//        return UserMapper.mapToUserResponse(user);
//    }


    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return UserMapper.mapToUserResponse(user);
    }

    public List<UserResponse> getAllUser() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(UserMapper::mapToUserResponse)
                .collect(Collectors.toList());
        List<UserDocument> documents = users.stream()
                .map(UserMapper::mapToUserDocument)
                .collect(Collectors.toList());
        userSearchRepository.saveAll(documents);
        return userResponses;
    }
}
