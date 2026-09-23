# TeamMate – Intelligent Team Formation System

## Overview

**TeamMate** is a Java-based intelligent team formation system developed for a university gaming club.

The system is designed to automatically form balanced and diverse teams for tournaments, friendly matches, and inter-university events. It uses participant information such as:

- Game / sport preference
- Skill level
- Preferred playing role
- Personality traits

The system processes participant data, classifies personality types, applies team formation constraints, and generates team assignments that can be viewed and exported as CSV files.

---

## Objectives

The main objectives of TeamMate are to:

- Reduce the manual effort required to form teams.
- Create balanced teams based on participant characteristics.
- Encourage diversity in games, roles, skills, and personality types.
- Validate participant information before processing.
- Support CSV-based participant data management.
- Handle invalid inputs and file-related errors.
- Support concurrent processing for team formation.
- Provide separate workflows for organizers and participants.

---

## Key Features

### 1. Participant Survey

Participants can enter their:

- Participant ID
- Name
- Email
- Phone number
- Personality survey responses
- Preferred game
- Preferred playing role
- Skill level

The system validates the entered information before storing it.

### 2. Personality Classification

The personality survey contains five questions using a rating scale.

The total personality score is used to classify participants into:

| Personality Type | Score Range |
|------------------|-------------|
| Leader | 90–100 |
| Balanced | 70–89 |
| Thinker | 50–69 |

### 3. Intelligent Team Formation

The team formation process considers multiple constraints, including:

- Personality distribution
- Game variety
- Role diversity
- Skill-level balance
- Team size

The implemented formation process distributes leaders first and then assigns remaining participants using constraint-based matching.

### 4. Configurable Team Size

The organizer can define the required team size before running the team formation process.

The system validates the selected team size against the available participant data and configured limits.

### 5. CSV File Handling

TeamMate supports CSV-based data processing.

The system can:

- Load participant data from CSV files.
- Validate imported data.
- Add participant information to CSV files.
- Update existing participant information.
- Export formed teams to CSV files.

### 6. Exception and Input Handling

The application includes validation and error handling for cases such as:

- Invalid participant IDs
- Missing information
- Invalid email formats
- Invalid phone numbers
- Invalid personality ratings
- Invalid skill levels
- Invalid games or roles
- Invalid team sizes
- Missing CSV files
- File read/write failures

### 7. Concurrent Processing

TeamMate includes concurrent processing for operations such as:

- Survey data processing
- Team formation

Concurrent execution is implemented using Java asynchronous processing mechanisms.

### 8. Organizer Portal

The organizer can:

1. Upload participant data.
2. Configure team formation parameters.
3. Run the team formation algorithm.
4. View formation results.
5. View team statistics.
6. Save formed teams to CSV.

### 9. Participant Portal

Participants can:

1. Complete the survey.
2. Update their information when required.
3. View their assigned team.
4. View teammate information and team details.

---

## System Workflow

```text
                    ┌─────────────────────┐
                    │      TeamMate       │
                    └──────────┬──────────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
        ┌───────▼────────┐           ┌────────▼────────┐
        │ Organizer       │           │ Participant     │
        │ Portal          │           │ Portal          │
        └───────┬─────────┘           └────────┬────────┘
                │                              │
                │                              │
        Upload Participant CSV           Complete Survey
                │                              │
                ▼                              ▼
        Validate Participant Data       Validate Input
                │                              │
                └──────────────┬───────────────┘
                               │
                               ▼
                    Personality Classification
                               │
                               ▼
                    Team Formation Parameters
                               │
                               ▼
                 Constraint-Based Team Formation
                               │
                               ▼
                     Validate Team Composition
                               │
                     ┌─────────┴─────────┐
                     │                   │
                     ▼                   ▼
             View Formation        Save Teams
                 Result               to CSV
                     │
                     ▼
              Team Assignments
                     │
                     ▼
              Participant Portal
