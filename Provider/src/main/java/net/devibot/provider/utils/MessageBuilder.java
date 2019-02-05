package net.devibot.provider.utils;

import net.devibot.core.entities.Emote;
import net.devibot.core.entities.Language;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.IMentionable;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.DataMessage;
import net.dv8tion.jda.core.utils.Checks;

import javax.annotation.CheckReturnValue;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class MessageBuilder implements Appendable {

    public static MessageBuilder SUCCESS_TEMPLATE = new MessageBuilder().setContent(Emote.SUCCESS + " | ");
    public static MessageBuilder FAILURE_TEMPLATE = new MessageBuilder().setContent(Emote.ERROR + " | ");
    public static MessageBuilder INFO_TEMPLATE = new MessageBuilder().setContent(Emote.INFO + " | ");

    private final StringBuilder builder = new StringBuilder();

    private boolean isTTS = false;
    private String nonce;
    private MessageEmbed embed;

    private Language language;
    private MessageChannel channel;

    public MessageBuilder() {
    }

    public MessageBuilder(CharSequence content) {
        if (content != null)
            builder.append(content);
    }

    public MessageBuilder(Message message) {
        if (message != null) {
            isTTS = message.isTTS();
            builder.append(message.getContentRaw());
            List<MessageEmbed> embeds = message.getEmbeds();
            if (embeds != null && !embeds.isEmpty())
                embed = embeds.get(0);
        }
    }

    public MessageBuilder(MessageBuilder builder) {
        if (builder != null) {
            this.isTTS = builder.isTTS;
            this.builder.append(builder.builder);
            this.nonce = builder.nonce;
            this.embed = builder.embed;
        }
    }

    public MessageBuilder(EmbedBuilder builder) {
        if (builder != null)
            this.embed = builder.build();
    }

    public MessageBuilder(MessageEmbed embed) {
        this.embed = embed;
    }

    public MessageBuilder(Language language) {
        this.language = language;
    }

    @CheckReturnValue
    public MessageBuilder setTTS(boolean tts) {
        this.isTTS = tts;
        return this;
    }

    @CheckReturnValue
    public MessageBuilder setEmbed(MessageEmbed embed) {
        this.embed = embed;
        return this;
    }

    @CheckReturnValue
    public MessageBuilder setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    @CheckReturnValue
    public MessageBuilder setContent(String content) {
        if (content == null) {
            builder.setLength(0);
        } else {
            Checks.check(content.length() <= Message.MAX_CONTENT_LENGTH, "Content length may not exceed %d!", Message.MAX_CONTENT_LENGTH);
            final int newLength = Math.max(builder.length(), content.length());
            builder.replace(0, newLength, content);
        }
        return this;
    }

    @CheckReturnValue
    public MessageBuilder setLanguage(Language language) {
        this.language = language;
        return this;
    }

    @CheckReturnValue
    public MessageBuilder setChannel(MessageChannel channel) {
        this.channel = channel;
        return this;
    }

    @CheckReturnValue
    public MessageBuilder appendTranslation(String key) {
        if (this.language == null)
            throw new UnsupportedOperationException("Can't request translation for language null");
        this.builder.append(Translator.getTranslation(this.language, key));
        return this;
    }

    @CheckReturnValue
    public MessageBuilder appendTranslation(String key, Object ... args) {
        if (this.language == null)
            throw new UnsupportedOperationException("Can't request translation for language null");
        this.builder.append(Translator.getTranslation(this.language, key, args));
        return this;
    }

    @CheckReturnValue
    @Override
    public MessageBuilder append(CharSequence text) {
        builder.append(text);
        return this;
    }

    @CheckReturnValue
    @Override
    public MessageBuilder append(CharSequence text, int start, int end) {
        builder.append(text, start, end);
        return this;
    }

    @CheckReturnValue
    @Override
    public MessageBuilder append(char c) {
        builder.append(c);
        return this;
    }

    @CheckReturnValue
    public MessageBuilder append(Object object) {
        return append(String.valueOf(object));
    }

    @CheckReturnValue
    public MessageBuilder append(IMentionable mention) {
        builder.append(mention.getAsMention());
        return this;
    }

    @CheckReturnValue
    public MessageBuilder append(CharSequence text, MessageBuilder.Formatting... format) {
        boolean blockPresent = false;
        for (MessageBuilder.Formatting formatting : format) {
            if (formatting == MessageBuilder.Formatting.BLOCK) {
                blockPresent = true;
                continue;
            }
            builder.append(formatting.getTag());
        }
        if (blockPresent)
            builder.append(MessageBuilder.Formatting.BLOCK.getTag());

        builder.append(text);

        if (blockPresent)
            builder.append(MessageBuilder.Formatting.BLOCK.getTag());
        for (int i = format.length - 1; i >= 0; i--) {
            if (format[i] == MessageBuilder.Formatting.BLOCK) continue;
            builder.append(format[i].getTag());
        }
        return this;
    }

    @CheckReturnValue
    public MessageBuilder appendFormat(String format, Object... args) {
        Checks.notEmpty(format, "Format String");
        this.append(String.format(format, args));
        return this;
    }

    @CheckReturnValue
    public MessageBuilder appendCodeLine(CharSequence text) {
        this.append(text, MessageBuilder.Formatting.BLOCK);
        return this;
    }

    @CheckReturnValue
    public MessageBuilder appendCodeBlock(CharSequence text, CharSequence language) {
        builder.append("```").append(language).append('\n').append(text).append("\n```");
        return this;
    }

    @CheckReturnValue
    public int length() {
        return builder.length();
    }

    @CheckReturnValue
    public boolean isEmpty() {
        return builder.length() == 0 && embed == null;
    }

    @CheckReturnValue
    public MessageBuilder replace(String target, String replacement) {
        int index = builder.indexOf(target);
        while (index != -1) {
            builder.replace(index, index + target.length(), replacement);
            index = builder.indexOf(target, index + replacement.length());
        }
        return this;
    }

    @CheckReturnValue
    public MessageBuilder replaceFirst(String target, String replacement) {
        int index = builder.indexOf(target);
        if (index != -1) {
            builder.replace(index, index + target.length(), replacement);
        }
        return this;
    }

    @CheckReturnValue
    public MessageBuilder replaceLast(String target, String replacement) {
        int index = builder.lastIndexOf(target);
        if (index != -1) {
            builder.replace(index, index + target.length(), replacement);
        }
        return this;
    }

    @CheckReturnValue
    public MessageBuilder stripMentions(JDA jda) {
        // Note: Users can rename to "everyone" or "here", so those
        // should be stripped after the USER mention is stripped.
        return this.stripMentions(jda, null, Message.MentionType.values());
    }

    @CheckReturnValue
    public MessageBuilder stripMentions(Guild guild) {
        // Note: Users can rename to "everyone" or "here", so those
        // should be stripped after the USER mention is stripped.
        return this.stripMentions(guild.getJDA(), guild, Message.MentionType.values());
    }

    @CheckReturnValue
    public MessageBuilder stripMentions(Guild guild, Message.MentionType... types) {
        return this.stripMentions(guild.getJDA(), guild, types);
    }

    @CheckReturnValue
    public MessageBuilder stripMentions(JDA jda, Message.MentionType... types) {
        return this.stripMentions(jda, null, types);
    }

    @CheckReturnValue
    private MessageBuilder stripMentions(JDA jda, Guild guild, Message.MentionType... types) {
        if (types == null)
            return this;

        String string = null;

        for (Message.MentionType mention : types) {
            if (mention != null) {
                switch (mention) {
                    case EVERYONE:
                        replace("@everyone", "@\u0435veryone");
                        break;
                    case HERE:
                        replace("@here", "@h\u0435re");
                        break;
                    case CHANNEL: {
                        if (string == null) {
                            string = builder.toString();
                        }

                        Matcher matcher = Message.MentionType.CHANNEL.getPattern().matcher(string);
                        while (matcher.find()) {
                            TextChannel channel = jda.getTextChannelById(matcher.group(1));
                            if (channel != null) {
                                replace(matcher.group(), "#" + channel.getName());
                            }
                        }
                        break;
                    }
                    case ROLE: {
                        if (string == null) {
                            string = builder.toString();
                        }

                        Matcher matcher = Message.MentionType.ROLE.getPattern().matcher(string);
                        while (matcher.find()) {
                            for (Guild g : jda.getGuilds()) {
                                Role role = g.getRoleById(matcher.group(1));
                                if (role != null) {
                                    replace(matcher.group(), "@" + role.getName());
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case USER: {
                        if (string == null) {
                            string = builder.toString();
                        }

                        Matcher matcher = Message.MentionType.USER.getPattern().matcher(string);
                        while (matcher.find()) {
                            User user = jda.getUserById(matcher.group(1));
                            String replacement;

                            if (user == null)
                                continue;

                            Member member;

                            if (guild != null && (member = guild.getMember(user)) != null)
                                replacement = member.getEffectiveName();
                            else
                                replacement = user.getName();

                            replace(matcher.group(), "@" + replacement);
                        }
                        break;
                    }
                }
            }
        }

        return this;
    }

    public StringBuilder getStringBuilder() {
        return this.builder;
    }

    public MessageBuilder clear() {
        this.builder.setLength(0);
        this.embed = null;
        this.isTTS = false;
        return this;
    }

    public int indexOf(CharSequence target, int fromIndex, int endIndex) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("index out of range: " + fromIndex);
        if (endIndex < 0)
            throw new IndexOutOfBoundsException("index out of range: " + endIndex);
        if (fromIndex > length())
            throw new IndexOutOfBoundsException("fromIndex > length()");
        if (fromIndex > endIndex)
            throw new IndexOutOfBoundsException("fromIndex > endIndex");

        if (endIndex >= builder.length()) {
            endIndex = builder.length() - 1;
        }

        int targetCount = target.length();
        if (targetCount == 0) {
            return fromIndex;
        }

        char strFirstChar = target.charAt(0);
        int max = endIndex + targetCount - 1;

        lastCharSearch:
        for (int i = fromIndex; i <= max; i++) {
            if (builder.charAt(i) == strFirstChar) {
                for (int j = 1; j < targetCount; j++) {
                    if (builder.charAt(i + j) != target.charAt(j)) {
                        continue lastCharSearch;
                    }
                }
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(CharSequence target, int fromIndex, int endIndex) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("index out of range: " + fromIndex);
        if (endIndex < 0)
            throw new IndexOutOfBoundsException("index out of range: " + endIndex);
        if (fromIndex > length())
            throw new IndexOutOfBoundsException("fromIndex > length()");
        if (fromIndex > endIndex)
            throw new IndexOutOfBoundsException("fromIndex > endIndex");

        if (endIndex >= builder.length()) {
            endIndex = builder.length() - 1;
        }

        int targetCount = target.length();
        if (targetCount == 0) {
            return endIndex;
        }

        int rightIndex = endIndex - targetCount;

        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }

        int strLastIndex = targetCount - 1;
        char strLastChar = target.charAt(strLastIndex);

        int min = fromIndex + targetCount - 1;

        lastCharSearch:
        for (int i = endIndex; i >= min; i--) {
            if (builder.charAt(i) == strLastChar) {
                for (int j = strLastIndex - 1, k = 1; j >= 0; j--, k++) {
                    if (builder.charAt(i - k) != target.charAt(j)) {
                        continue lastCharSearch;
                    }
                }
                return i - target.length() + 1;
            }
        }
        return -1;
    }

    public void execute() {
        execute(null, null);
    }

    public void execute(Consumer<? super Message> success) {
        execute(success, null);
    }

    public void execute(Consumer<? super Message> success, Consumer<? super Throwable> failure) {
        if (this.channel == null)
            throw new UnsupportedOperationException("Cannot send message to null channel.");
        MessageUtils.sendMessage(channel, build(), success, failure);
    }


    public Message build() {
        String message = builder.toString();
        if (this.isEmpty())
            throw new IllegalStateException("Cannot build a Message with no content. (You never added any content to the message)");
        if (message.length() > Message.MAX_CONTENT_LENGTH)
            throw new IllegalStateException("Cannot build a Message with more than 2000 characters. Please limit your input.");

        return new DataMessage(isTTS, message, nonce, embed);
    }

    public Queue<Message> buildAll(MessageBuilder.SplitPolicy... policy) {
        if (this.isEmpty())
            throw new UnsupportedOperationException("Cannot build a Message with no content. (You never added any content to the message)");

        LinkedList<Message> messages = new LinkedList<>();

        if (builder.length() <= 2000) {
            messages.add(this.build());
            return messages;
        }

        if (policy == null || policy.length == 0) {
            policy = new MessageBuilder.SplitPolicy[]{MessageBuilder.SplitPolicy.ANYWHERE};
        }

        int currentBeginIndex = 0;

        messageLoop:
        while (currentBeginIndex < builder.length() - 2001) {
            for (int i = 0; i < policy.length; i++) {
                int currentEndIndex = policy[i].nextMessage(currentBeginIndex, this);
                if (currentEndIndex != -1) {
                    messages.add(build(currentBeginIndex, currentEndIndex));
                    currentBeginIndex = currentEndIndex;
                    continue messageLoop;
                }
            }
            throw new IllegalStateException("Failed to split the messages");
        }

        if (currentBeginIndex < builder.length() - 1) {
            messages.add(build(currentBeginIndex, builder.length() - 1));
        }

        if (this.embed != null) {
            ((DataMessage) messages.get(messages.size() - 1)).setEmbed(embed);
        }

        return messages;
    }

    public DataMessage build(int beginIndex, int endIndex) {
        return new DataMessage(isTTS, builder.substring(beginIndex, endIndex), null, null);
    }

    public interface SplitPolicy {
        MessageBuilder.SplitPolicy NEWLINE = new MessageBuilder.SplitPolicy.CharSequenceSplitPolicy("\n", true);

        MessageBuilder.SplitPolicy SPACE = new MessageBuilder.SplitPolicy.CharSequenceSplitPolicy(" ", true);

        MessageBuilder.SplitPolicy ANYWHERE = (i, b) -> Math.min(i + 2000, b.length());

        static MessageBuilder.SplitPolicy onChars(CharSequence chars, boolean remove) {
            return new MessageBuilder.SplitPolicy.CharSequenceSplitPolicy(chars, remove);
        }

        class CharSequenceSplitPolicy implements MessageBuilder.SplitPolicy {
            private final boolean remove;
            private final CharSequence chars;

            private CharSequenceSplitPolicy(final CharSequence chars, final boolean remove) {
                this.chars = chars;
                this.remove = remove;
            }

            @Override
            public int nextMessage(final int currentBeginIndex, final MessageBuilder builder) {
                int currentEndIndex = builder.lastIndexOf(this.chars, currentBeginIndex, currentBeginIndex + 2000 - (this.remove ? this.chars.length() : 0));
                if (currentEndIndex < 0) {
                    return -1;
                } else {
                    return currentEndIndex + this.chars.length();
                }
            }
        }

        int nextMessage(int currentBeginIndex, MessageBuilder builder);
    }

    public enum Formatting {
        ITALICS("*"),
        BOLD("**"),
        STRIKETHROUGH("~~"),
        UNDERLINE("__"),
        BLOCK("`");

        private final String tag;

        Formatting(String tag) {
            this.tag = tag;
        }

        private String getTag() {
            return tag;
        }
    }


}
