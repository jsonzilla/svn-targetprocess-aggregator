--
-- PostgreSQL database dump
--

-- Dumped from database version 14.4
-- Dumped by pg_dump version 14.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: API_KEY; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."API_KEY" (
    api_key character varying NOT NULL,
    name character varying NOT NULL,
    active boolean NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public."API_KEY" OWNER TO postgres;

--
-- Name: API_KEY_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."API_KEY_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."API_KEY_id_seq" OWNER TO postgres;

--
-- Name: API_KEY_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."API_KEY_id_seq" OWNED BY public."API_KEY".id;


--
-- Name: API_LOG; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."API_LOG" (
    date timestamp without time zone NOT NULL,
    ip character varying NOT NULL,
    api_key character varying,
    token character varying,
    method character varying NOT NULL,
    uri character varying NOT NULL,
    request_body character varying,
    response_status integer NOT NULL,
    response_body character varying,
    id bigint NOT NULL
);


ALTER TABLE public."API_LOG" OWNER TO postgres;

--
-- Name: API_LOG_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."API_LOG_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."API_LOG_id_seq" OWNER TO postgres;

--
-- Name: API_LOG_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."API_LOG_id_seq" OWNED BY public."API_LOG".id;


--
-- Name: API_TOKENS; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."API_TOKENS" (
    token character varying NOT NULL,
    api_key character varying NOT NULL,
    expiration_time timestamp without time zone NOT NULL,
    user_id bigint NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public."API_TOKENS" OWNER TO postgres;

--
-- Name: API_TOKENS_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."API_TOKENS_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."API_TOKENS_id_seq" OWNER TO postgres;

--
-- Name: API_TOKENS_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."API_TOKENS_id_seq" OWNED BY public."API_TOKENS".id;


--
-- Name: AUTHORS; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."AUTHORS" (
    author character varying NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public."AUTHORS" OWNER TO postgres;

--
-- Name: AUTHORS_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."AUTHORS_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."AUTHORS_id_seq" OWNER TO postgres;

--
-- Name: AUTHORS_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."AUTHORS_id_seq" OWNED BY public."AUTHORS".id;


--
-- Name: COMMITFILES; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."COMMITFILES" (
    "typeModification" integer,
    "copyPath_id" bigint,
    "copyRevision" bigint,
    path_id bigint NOT NULL,
    revision bigint NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public."COMMITFILES" OWNER TO postgres;

--
-- Name: COMMITFILES_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."COMMITFILES_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."COMMITFILES_id_seq" OWNER TO postgres;

--
-- Name: COMMITFILES_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."COMMITFILES_id_seq" OWNED BY public."COMMITFILES".id;


--
-- Name: COMMITS; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."COMMITS" (
    message character varying,
    "timestamp" timestamp without time zone,
    revision bigint NOT NULL,
    author bigint NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public."COMMITS" OWNER TO postgres;

--
-- Name: COMMITS_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."COMMITS_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."COMMITS_id_seq" OWNER TO postgres;

--
-- Name: COMMITS_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."COMMITS_id_seq" OWNED BY public."COMMITS".id;


--
-- Name: COMMITTASKS; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."COMMITTASKS" (
    task_id bigint NOT NULL,
    commit_id bigint NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public."COMMITTASKS" OWNER TO postgres;

--
-- Name: COMMITTASKS_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."COMMITTASKS_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."COMMITTASKS_id_seq" OWNER TO postgres;

--
-- Name: COMMITTASKS_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."COMMITTASKS_id_seq" OWNED BY public."COMMITTASKS".id;


--
-- Name: CUSTOM_FIELDS; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."CUSTOM_FIELDS" (
    field_value character varying,
    field character varying NOT NULL,
    task_id bigint NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public."CUSTOM_FIELDS" OWNER TO postgres;

--
-- Name: CUSTOM_FIELDS_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."CUSTOM_FIELDS_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."CUSTOM_FIELDS_id_seq" OWNER TO postgres;

--
-- Name: CUSTOM_FIELDS_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."CUSTOM_FIELDS_id_seq" OWNED BY public."CUSTOM_FIELDS".id;


--
-- Name: FILES; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."FILES" (
    path character varying NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public."FILES" OWNER TO postgres;

--
-- Name: FILES_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."FILES_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."FILES_id_seq" OWNER TO postgres;

--
-- Name: FILES_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."FILES_id_seq" OWNED BY public."FILES".id;


--
-- Name: TASK; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."TASK" (
    type_task character varying,
    type_task_id bigint,
    user_story bigint,
    time_spend double precision,
    parent_id bigint,
    task_id bigint NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public."TASK" OWNER TO postgres;

--
-- Name: TASK_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."TASK_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."TASK_id_seq" OWNER TO postgres;

--
-- Name: TASK_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."TASK_id_seq" OWNED BY public."TASK".id;


--
-- Name: USERS; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."USERS" (
    email character varying NOT NULL,
    password character varying NOT NULL,
    name character varying NOT NULL,
    "emailConfirmed" boolean NOT NULL,
    active boolean NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE public."USERS" OWNER TO postgres;

--
-- Name: USERS_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."USERS_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."USERS_id_seq" OWNER TO postgres;

--
-- Name: USERS_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."USERS_id_seq" OWNED BY public."USERS".id;


--
-- Name: API_KEY id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."API_KEY" ALTER COLUMN id SET DEFAULT nextval('public."API_KEY_id_seq"'::regclass);


--
-- Name: API_LOG id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."API_LOG" ALTER COLUMN id SET DEFAULT nextval('public."API_LOG_id_seq"'::regclass);


--
-- Name: API_TOKENS id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."API_TOKENS" ALTER COLUMN id SET DEFAULT nextval('public."API_TOKENS_id_seq"'::regclass);


--
-- Name: AUTHORS id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."AUTHORS" ALTER COLUMN id SET DEFAULT nextval('public."AUTHORS_id_seq"'::regclass);


--
-- Name: COMMITFILES id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITFILES" ALTER COLUMN id SET DEFAULT nextval('public."COMMITFILES_id_seq"'::regclass);


--
-- Name: COMMITS id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITS" ALTER COLUMN id SET DEFAULT nextval('public."COMMITS_id_seq"'::regclass);


--
-- Name: COMMITTASKS id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITTASKS" ALTER COLUMN id SET DEFAULT nextval('public."COMMITTASKS_id_seq"'::regclass);


--
-- Name: CUSTOM_FIELDS id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."CUSTOM_FIELDS" ALTER COLUMN id SET DEFAULT nextval('public."CUSTOM_FIELDS_id_seq"'::regclass);


--
-- Name: FILES id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."FILES" ALTER COLUMN id SET DEFAULT nextval('public."FILES_id_seq"'::regclass);


--
-- Name: TASK id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."TASK" ALTER COLUMN id SET DEFAULT nextval('public."TASK_id_seq"'::regclass);


--
-- Name: USERS id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."USERS" ALTER COLUMN id SET DEFAULT nextval('public."USERS_id_seq"'::regclass);


--
-- Name: API_KEY API_KEY_api_key_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."API_KEY"
    ADD CONSTRAINT "API_KEY_api_key_key" UNIQUE (api_key);


--
-- Name: API_KEY API_KEY_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."API_KEY"
    ADD CONSTRAINT "API_KEY_pkey" PRIMARY KEY (id);


--
-- Name: API_LOG API_LOG_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."API_LOG"
    ADD CONSTRAINT "API_LOG_pkey" PRIMARY KEY (id);


--
-- Name: API_TOKENS API_TOKENS_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."API_TOKENS"
    ADD CONSTRAINT "API_TOKENS_pkey" PRIMARY KEY (id);


--
-- Name: API_TOKENS API_TOKENS_token_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."API_TOKENS"
    ADD CONSTRAINT "API_TOKENS_token_key" UNIQUE (token);


--
-- Name: AUTHORS AUTHORS_author_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."AUTHORS"
    ADD CONSTRAINT "AUTHORS_author_key" UNIQUE (author);


--
-- Name: AUTHORS AUTHORS_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."AUTHORS"
    ADD CONSTRAINT "AUTHORS_pkey" PRIMARY KEY (id);


--
-- Name: COMMITFILES COMMITFILES_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITFILES"
    ADD CONSTRAINT "COMMITFILES_pkey" PRIMARY KEY (id);


--
-- Name: COMMITS COMMITS_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITS"
    ADD CONSTRAINT "COMMITS_pkey" PRIMARY KEY (id);


--
-- Name: COMMITS COMMITS_revision_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITS"
    ADD CONSTRAINT "COMMITS_revision_key" UNIQUE (revision);


--
-- Name: COMMITTASKS COMMITTASKS_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITTASKS"
    ADD CONSTRAINT "COMMITTASKS_pkey" PRIMARY KEY (id);


--
-- Name: CUSTOM_FIELDS CUSTOM_FIELDS_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."CUSTOM_FIELDS"
    ADD CONSTRAINT "CUSTOM_FIELDS_pkey" PRIMARY KEY (id);


--
-- Name: CUSTOM_FIELDS CUSTOM_FIELDS_task_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."CUSTOM_FIELDS"
    ADD CONSTRAINT "CUSTOM_FIELDS_task_id_key" UNIQUE (task_id);


--
-- Name: FILES FILES_path_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."FILES"
    ADD CONSTRAINT "FILES_path_key" UNIQUE (path);


--
-- Name: FILES FILES_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."FILES"
    ADD CONSTRAINT "FILES_pkey" PRIMARY KEY (id);


--
-- Name: TASK TASK_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."TASK"
    ADD CONSTRAINT "TASK_pkey" PRIMARY KEY (id);


--
-- Name: TASK TASK_task_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."TASK"
    ADD CONSTRAINT "TASK_task_id_key" UNIQUE (task_id);


--
-- Name: USERS USERS_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."USERS"
    ADD CONSTRAINT "USERS_email_key" UNIQUE (email);


--
-- Name: USERS USERS_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."USERS"
    ADD CONSTRAINT "USERS_pkey" PRIMARY KEY (id);


--
-- Name: COMMITS author_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITS"
    ADD CONSTRAINT author_fk FOREIGN KEY (author) REFERENCES public."AUTHORS"(id) ON DELETE SET NULL;


--
-- Name: COMMITTASKS commit_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITTASKS"
    ADD CONSTRAINT commit_fk FOREIGN KEY (commit_id) REFERENCES public."COMMITS"(id) ON DELETE CASCADE;


--
-- Name: COMMITFILES path_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITFILES"
    ADD CONSTRAINT path_fk FOREIGN KEY (path_id) REFERENCES public."FILES"(id) ON DELETE CASCADE;


--
-- Name: COMMITFILES revision_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."COMMITFILES"
    ADD CONSTRAINT revision_fk FOREIGN KEY (revision) REFERENCES public."COMMITS"(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

