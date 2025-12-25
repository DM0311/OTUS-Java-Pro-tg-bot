create sequence users_SEQ start with 1 increment by 1;
create sequence sessions_SEQ start with 1 increment by 1;
create sequence questions_SEQ start with 1 increment by 1;
create sequence answers_SEQ start with 1 increment by 1;
create sequence exam_answers_SEQ start with 1 increment by 1;

-- users
create table if not exists users (
    id  bigint primary key default nextval('users_SEQ'),
    telegram_id bigint unique not null,
    chat_id bigint not null,
    username varchar(50),
    first_name varchar(50),
    last_name varchar(50),
    state varchar(20) default 'MAIN_MENU',
    -- statistics
    total_exams integer default 0,
    passed_exams integer default 0,
    total_questions_answered integer default 0,
    correct_answers integer default 0,
    is_active BOOLEAN default true,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);
-- exam sessions
create table if not exists exam_sessions (
    id  bigint primary key default nextval('sessions_SEQ'),
    user_id bigint not null,
    status varchar(20) default 'IN_PROGRESS',
    started_at timestamp default current_timestamp,
    completed_at timestamp,
    total_questions integer not null,
    correct_answers integer default 0,
    current_question_index integer default 0,
    passed BOOLEAN default false,
    created_at timestamp default current_timestamp,

    constraint fk_exam_session_user foreign key (user_id)
        references users(id) on delete cascade
);
-- questions
create table if not exists questions (
    id bigint primary key default nextval('questions_SEQ'),
    text TEXT not null,
    image_url varchar(200),
    topic varchar(50),
    explanation TEXT,
    difficulty INTEGER default 1,

    -- statistics
    times_shown integer default 0,
    times_correct integer default 0,
    times_wrong integer default 0,
    is_active boolean default true,
    created_at timestamp default current_timestamp
);
-- answers
create table if not exists answers (
    id bigint primary key default nextval('answers_SEQ'),
    question_id bigint not null,
    text varchar(500) not null,
    letter char(1) not null check (letter in ('A', 'B', 'C', 'D')),
    is_correct BOOLEAN default false,
    created_at timestamp default current_timestamp,

    constraint fk_answer_question foreign key (question_id)
        references questions(id) on delete cascade,
    constraint unique_question_letter unique (question_id, letter)
);
-- answers in exams
create table if not exists exam_answers (
    id bigint primary key default nextval('exam_answers_SEQ'),
    exam_session_id bigint not null,
    user_id bigint not null,
    question_id bigint not null,
    selected_answer_id bigint,
    is_correct BOOLEAN default false,
    answered_at timestamp default current_timestamp,
    created_at timestamp default current_timestamp,

    constraint fk_exam_answer_session foreign key (exam_session_id)
        references exam_sessions(id) on delete cascade,
    constraint fk_exam_answer_user foreign key (user_id)
        references users(id) on delete cascade,
    constraint fk_exam_answer_question foreign key (question_id)
        references questions(id),
    constraint fk_exam_answer_selected foreign key (selected_answer_id)
        references answers(id)
);