package com.example.movieticket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws Exception {
        if (isDatabaseAlreadyInitialized()) {
            System.out.println("========================================");
            System.out.println("数据库已初始化，跳过初始化步骤");
            System.out.println("========================================");
            addMissingColumns();
            return;
        }

        System.out.println("========================================");
        System.out.println("开始初始化数据...");
        System.out.println("========================================");

        try {
            System.out.println("[步骤1] 创建数据库表...");
            executeSqlScript("classpath:schema.sql");
            System.out.println("[步骤1] 数据库表创建成功！");

            System.out.println("[步骤2] 插入初始数据...");
            executeSqlScript("classpath:data.sql");
            System.out.println("[步骤2] 数据插入成功！");

            System.out.println("[步骤3] 验证数据...");
            verifyData();

            System.out.println("========================================");
            System.out.println("数据初始化全部完成！");
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("数据初始化失败：" + e.getMessage());
            System.err.println("========================================");
            throw e;
        }
    }

    private boolean isDatabaseAlreadyInitialized() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM banner", Integer.class);
            // 检查 comment_like 表是否存在
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comment_like", Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void executeSqlScript(String scriptPath) throws IOException {
        var resource = resourceLoader.getResource(scriptPath);

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            String sql = FileCopyUtils.copyToString(reader);

            String[] parts = sql.split(";");

            List<String> statements = new ArrayList<>();
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    statements.add(trimmed);
                }
            }

            int count = 0;
            for (int i = 0; i < statements.size(); i++) {
                count++;
                String stmt = statements.get(i);
                String preview = stmt.length() > 80 ? stmt.substring(0, 80) + "..." : stmt;
                System.out.println("  执行SQL #" + count + ": " + preview.replace("\n", " ").replace("\r", " "));
                jdbcTemplate.execute(stmt);
                System.out.println("  -> 成功");
            }
            System.out.println("  共执行 " + count + " 条SQL语句");
        }
    }

    private void verifyData() {
        try {
            int bannerCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM banner", Integer.class);
            System.out.println("  轮播图: " + bannerCount + " 条");

            int movieCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM movie", Integer.class);
            System.out.println("  电影: " + movieCount + " 条");

            int cinemaCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cinema", Integer.class);
            System.out.println("  影院: " + cinemaCount + " 条");
        } catch (Exception e) {
            System.err.println("  验证失败: " + e.getMessage());
        }
    }

    private void addMissingColumns() {
        try {
            jdbcTemplate.queryForObject("SELECT role FROM sys_user LIMIT 1", String.class);
            System.out.println("sys_user表已存在role字段，无需添加");
        } catch (Exception e) {
            try {
                jdbcTemplate.execute("ALTER TABLE sys_user ADD COLUMN role VARCHAR(20) DEFAULT 'user'");
                System.out.println("已为sys_user表添加role字段");
            } catch (Exception ex) {
                System.err.println("添加role字段失败: " + ex.getMessage());
            }
        }
    }
}