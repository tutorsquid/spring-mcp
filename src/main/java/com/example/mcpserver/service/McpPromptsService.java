package com.example.mcpserver.service;

import org.springframework.ai.mcp.server.McpPrompt;
import org.springframework.ai.mcp.server.McpPromptParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MCP Prompts service providing reusable prompt templates.
 * Prompts help structure interactions with language models.
 * Uses Spring AI MCP annotations for automatic prompt registration.
 */
@Service
public class McpPromptsService {

    /**
     * Example 1: Simple greeting prompt
     * Name: greeting
     */
    @McpPrompt(
        name = "greeting",
        description = "Generate a personalized greeting message"
    )
    public String generateGreeting(
            @McpPromptParam(description = "The name of the person to greet", required = true) String name,
            @McpPromptParam(description = "The time of day (morning, afternoon, evening)", required = false) String timeOfDay) {

        String greeting = timeOfDay != null && !timeOfDay.isEmpty()
            ? String.format("Good %s", timeOfDay)
            : "Hello";

        return String.format("%s, %s! How can I assist you today?", greeting, name);
    }

    /**
     * Example 2: Code review prompt template
     * Name: code-review
     */
    @McpPrompt(
        name = "code-review",
        description = "Generate a comprehensive code review prompt for analyzing code"
    )
    public String generateCodeReviewPrompt(
            @McpPromptParam(description = "The programming language of the code", required = true) String language,
            @McpPromptParam(description = "Specific aspects to focus on (e.g., security, performance, readability)", required = false) String focusArea) {

        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format("Please review the following %s code:\n\n", language));
        prompt.append("Focus on the following aspects:\n");
        prompt.append("- Code quality and readability\n");
        prompt.append("- Best practices and design patterns\n");
        prompt.append("- Potential bugs or edge cases\n");

        if (focusArea != null && !focusArea.isEmpty()) {
            prompt.append(String.format("- Special focus: %s\n", focusArea));
        }

        prompt.append("\nProvide:\n");
        prompt.append("1. Overall assessment\n");
        prompt.append("2. Specific issues found (if any)\n");
        prompt.append("3. Suggestions for improvement\n");
        prompt.append("4. Positive aspects of the code\n");

        return prompt.toString();
    }

    /**
     * Example 3: Data analysis prompt
     * Name: analyze-data
     */
    @McpPrompt(
        name = "analyze-data",
        description = "Generate a prompt for analyzing data or datasets"
    )
    public String generateDataAnalysisPrompt(
            @McpPromptParam(description = "The type of data being analyzed", required = true) String dataType,
            @McpPromptParam(description = "The analysis goal or question", required = true) String goal,
            @McpPromptParam(description = "Additional context about the data", required = false) String context) {

        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format("Analyze the following %s data:\n\n", dataType));

        if (context != null && !context.isEmpty()) {
            prompt.append(String.format("Context: %s\n\n", context));
        }

        prompt.append(String.format("Analysis Goal: %s\n\n", goal));
        prompt.append("Please provide:\n");
        prompt.append("1. Summary statistics and key findings\n");
        prompt.append("2. Patterns or trends identified\n");
        prompt.append("3. Anomalies or outliers (if any)\n");
        prompt.append("4. Insights and recommendations\n");
        prompt.append("5. Suggested visualizations\n");

        return prompt.toString();
    }

    /**
     * Example 4: Meeting summary prompt
     * Name: meeting-summary
     */
    @McpPrompt(
        name = "meeting-summary",
        description = "Generate a prompt for creating structured meeting summaries"
    )
    public String generateMeetingSummaryPrompt(
            @McpPromptParam(description = "The meeting topic or title", required = true) String topic,
            @McpPromptParam(description = "List of participants (comma-separated)", required = false) String participants) {

        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format("Create a structured summary for the meeting: \"%s\"\n\n", topic));

        if (participants != null && !participants.isEmpty()) {
            prompt.append(String.format("Participants: %s\n\n", participants));
        }

        prompt.append("Please organize the summary into the following sections:\n\n");
        prompt.append("## Meeting Overview\n");
        prompt.append("- Date and duration\n");
        prompt.append("- Main objective\n\n");
        prompt.append("## Key Discussion Points\n");
        prompt.append("- [List main topics discussed]\n\n");
        prompt.append("## Decisions Made\n");
        prompt.append("- [List all decisions]\n\n");
        prompt.append("## Action Items\n");
        prompt.append("- [List with assignees and deadlines]\n\n");
        prompt.append("## Next Steps\n");
        prompt.append("- [Upcoming activities]\n");

        return prompt.toString();
    }

    /**
     * Example 5: Debug helper prompt
     * Name: debug-helper
     */
    @McpPrompt(
        name = "debug-helper",
        description = "Generate a systematic debugging prompt for troubleshooting issues"
    )
    public String generateDebugPrompt(
            @McpPromptParam(description = "The error message or issue description", required = true) String issue,
            @McpPromptParam(description = "The technology stack or environment", required = true) String stack,
            @McpPromptParam(description = "What has been tried already", required = false) String attemptedSolutions) {

        StringBuilder prompt = new StringBuilder();
        prompt.append("Help me debug the following issue:\n\n");
        prompt.append(String.format("**Issue:** %s\n\n", issue));
        prompt.append(String.format("**Stack:** %s\n\n", stack));

        if (attemptedSolutions != null && !attemptedSolutions.isEmpty()) {
            prompt.append(String.format("**Already Tried:** %s\n\n", attemptedSolutions));
        }

        prompt.append("Please provide:\n");
        prompt.append("1. Possible root causes\n");
        prompt.append("2. Step-by-step debugging approach\n");
        prompt.append("3. Specific things to check or test\n");
        prompt.append("4. Recommended fixes or workarounds\n");
        prompt.append("5. How to prevent this issue in the future\n");

        return prompt.toString();
    }

    /**
     * Example 6: Documentation generator prompt
     * Name: generate-docs
     */
    @McpPrompt(
        name = "generate-docs",
        description = "Generate a prompt for creating technical documentation"
    )
    public String generateDocumentationPrompt(
            @McpPromptParam(description = "The component or feature to document", required = true) String component,
            @McpPromptParam(description = "Target audience (developers, users, admins)", required = true) String audience,
            @McpPromptParam(description = "Documentation format (markdown, html, javadoc)", required = false) String format) {

        String formatType = format != null && !format.isEmpty() ? format : "markdown";

        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format("Create technical documentation for: %s\n\n", component));
        prompt.append(String.format("Target Audience: %s\n", audience));
        prompt.append(String.format("Format: %s\n\n", formatType));

        prompt.append("Include the following sections:\n\n");
        prompt.append("1. **Overview**\n");
        prompt.append("   - What it is\n");
        prompt.append("   - Key features\n");
        prompt.append("   - Use cases\n\n");
        prompt.append("2. **Getting Started**\n");
        prompt.append("   - Prerequisites\n");
        prompt.append("   - Installation/Setup\n");
        prompt.append("   - Quick start example\n\n");
        prompt.append("3. **API/Interface Reference**\n");
        prompt.append("   - Methods/Functions\n");
        prompt.append("   - Parameters\n");
        prompt.append("   - Return values\n\n");
        prompt.append("4. **Examples**\n");
        prompt.append("   - Common use cases\n");
        prompt.append("   - Code samples\n\n");
        prompt.append("5. **Best Practices**\n");
        prompt.append("   - Recommendations\n");
        prompt.append("   - Common pitfalls\n\n");
        prompt.append("6. **Troubleshooting**\n");
        prompt.append("   - Common issues\n");
        prompt.append("   - Solutions\n");

        return prompt.toString();
    }

    /**
     * Example 7: SQL query builder prompt
     * Name: sql-helper
     */
    @McpPrompt(
        name = "sql-helper",
        description = "Generate a prompt for building SQL queries based on requirements"
    )
    public String generateSqlHelperPrompt(
            @McpPromptParam(description = "Description of the data to retrieve or modify", required = true) String requirement,
            @McpPromptParam(description = "The database type (MySQL, PostgreSQL, etc.)", required = false) String dbType,
            @McpPromptParam(description = "Performance considerations (indexes, optimization)", required = false) String performance) {

        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a SQL query for the following requirement:\n\n");
        prompt.append(String.format("**Requirement:** %s\n\n", requirement));

        if (dbType != null && !dbType.isEmpty()) {
            prompt.append(String.format("**Database Type:** %s\n\n", dbType));
        }

        prompt.append("Please provide:\n");
        prompt.append("1. The SQL query\n");
        prompt.append("2. Explanation of the query structure\n");
        prompt.append("3. Any assumptions made\n");

        if (performance != null && !performance.isEmpty()) {
            prompt.append("4. Performance optimization tips\n");
            prompt.append("5. Recommended indexes\n");
        }

        return prompt.toString();
    }
}
