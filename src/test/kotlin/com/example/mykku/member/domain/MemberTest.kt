package com.example.mykku.member.domain

import com.example.mykku.member.domain.entity.Member
import com.example.mykku.member.domain.vo.SocialProvider
import com.example.mykku.member.exception.MemberErrorCode
import com.example.mykku.member.exception.MemberException
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Member 도메인 엔티티 테스트")
class MemberTest {

    @Nested
    @DisplayName("createEmailMember 메서드")
    inner class CreateEmailMember {

        @Test
        @DisplayName("정상적으로 이메일 회원을 생성한다")
        fun `이메일 회원 생성 - 정상 케이스`() {
            val member = Member.createEmailMember(
                id = "uuid-1234",
                memberId = "testuser1",
                email = "test@example.com",
                password = "encodedPassword",
                nickname = "테스트닉네임"
            )

            assertThat(member.id.value).isEqualTo("uuid-1234")
            assertThat(member.memberId).isEqualTo("testuser1")
            assertThat(member.email).isEqualTo("test@example.com")
            assertThat(member.nickname).isEqualTo("테스트닉네임")
            assertThat(member.provider).isEqualTo(SocialProvider.EMAIL)
        }

        @Test
        @DisplayName("이메일 회원 생성시 emailVerified가 false이다")
        fun `이메일 회원 생성 - 이메일 미인증 상태`() {
            val member = createEmailMember()

            assertThat(member.emailVerified).isFalse()
        }

        @Test
        @DisplayName("이메일 회원 생성시 socialId가 null이다")
        fun `이메일 회원 생성 - socialId null`() {
            val member = createEmailMember()

            assertThat(member.socialId).isNull()
        }

        @Test
        @DisplayName("이메일 회원 생성시 createdAt과 updatedAt이 설정된다")
        fun `이메일 회원 생성 - 시간 설정 검증`() {
            val member = createEmailMember()

            assertThat(member.createdAt).isNotNull()
            assertThat(member.updatedAt).isNotNull()
        }
    }

    @Nested
    @DisplayName("createSocialMember 메서드")
    inner class CreateSocialMember {

        @Test
        @DisplayName("정상적으로 소셜 회원을 생성한다")
        fun `소셜 회원 생성 - 정상 케이스`() {
            val member = Member.createSocialMember(
                id = "uuid-1234",
                memberId = "kakaouser1",
                nickname = "카카오유저",
                profileImage = "https://example.com/profile.jpg",
                provider = SocialProvider.KAKAO,
                socialId = "kakao-12345",
                email = "kakao@example.com"
            )

            assertThat(member.provider).isEqualTo(SocialProvider.KAKAO)
            assertThat(member.socialId).isEqualTo("kakao-12345")
        }

        @Test
        @DisplayName("소셜 회원 생성시 emailVerified가 true이다")
        fun `소셜 회원 생성 - 이메일 인증 상태`() {
            val member = createSocialMember()

            assertThat(member.emailVerified).isTrue()
        }

        @Test
        @DisplayName("소셜 회원 생성시 password가 null이다")
        fun `소셜 회원 생성 - password null`() {
            val member = createSocialMember()

            assertThat(member.password).isNull()
        }
    }

    @Nested
    @DisplayName("validateMemberId 메서드")
    inner class ValidateMemberId {

        @Test
        @DisplayName("아이디가 빈 문자열이면 예외가 발생한다")
        fun `아이디 검증 - 빈 문자열`() {
            val exception = assertThrows<MemberException> {
                Member.validateMemberId("")
            }

            assertThat(exception.errorCode).isEqualTo(MemberErrorCode.MEMBER_ID_EMPTY)
        }

        @Test
        @DisplayName("아이디가 공백만 있으면 예외가 발생한다")
        fun `아이디 검증 - 공백만`() {
            val exception = assertThrows<MemberException> {
                Member.validateMemberId("   ")
            }

            assertThat(exception.errorCode).isEqualTo(MemberErrorCode.MEMBER_ID_EMPTY)
        }

        @Test
        @DisplayName("아이디가 16자를 초과하면 예외가 발생한다")
        fun `아이디 검증 - 길이 초과`() {
            val longMemberId = "a".repeat(Member.MEMBER_ID_MAX_LENGTH + 1)

            val exception = assertThrows<MemberException> {
                Member.validateMemberId(longMemberId)
            }

            assertThat(exception.errorCode).isEqualTo(MemberErrorCode.MEMBER_ID_TOO_LONG)
        }

        @Test
        @DisplayName("아이디가 정확히 16자일 때는 통과한다")
        fun `아이디 검증 - 길이 경계값`() {
            val exactMemberId = "a".repeat(Member.MEMBER_ID_MAX_LENGTH)

            Member.validateMemberId(exactMemberId)
        }

        @Test
        @DisplayName("아이디에 특수문자가 포함되면 예외가 발생한다")
        fun `아이디 검증 - 특수문자 포함`() {
            val exception = assertThrows<MemberException> {
                Member.validateMemberId("test@user")
            }

            assertThat(exception.errorCode).isEqualTo(MemberErrorCode.MEMBER_ID_INVALID_FORMAT)
        }

        @Test
        @DisplayName("아이디에 한글이 포함되면 예외가 발생한다")
        fun `아이디 검증 - 한글 포함`() {
            val exception = assertThrows<MemberException> {
                Member.validateMemberId("테스트user")
            }

            assertThat(exception.errorCode).isEqualTo(MemberErrorCode.MEMBER_ID_INVALID_FORMAT)
        }

        @Test
        @DisplayName("영문과 숫자로만 구성된 아이디는 통과한다")
        fun `아이디 검증 - 정상 케이스`() {
            Member.validateMemberId("testUser123")
        }
    }

    @Nested
    @DisplayName("닉네임 검증")
    inner class NicknameValidation {

        @Test
        @DisplayName("닉네임이 10자를 초과하면 예외가 발생한다")
        fun `닉네임 검증 - 길이 초과`() {
            val longNickname = "가".repeat(Member.NICKNAME_MAX_LENGTH + 1)

            val exception = assertThrows<MemberException> {
                Member.createEmailMember(
                    id = "uuid-1234",
                    memberId = "testuser1",
                    email = "test@example.com",
                    password = "encodedPassword",
                    nickname = longNickname
                )
            }

            assertThat(exception.errorCode).isEqualTo(MemberErrorCode.MEMBER_NICKNAME_TOO_LONG)
        }

        @Test
        @DisplayName("닉네임이 정확히 10자일 때는 통과한다")
        fun `닉네임 검증 - 길이 경계값`() {
            val exactNickname = "가".repeat(Member.NICKNAME_MAX_LENGTH)

            val member = Member.createEmailMember(
                id = "uuid-1234",
                memberId = "testuser1",
                email = "test@example.com",
                password = "encodedPassword",
                nickname = exactNickname
            )

            assertThat(member.nickname.length).isEqualTo(Member.NICKNAME_MAX_LENGTH)
        }

        @Test
        @DisplayName("닉네임에 특수문자가 포함되면 예외가 발생한다")
        fun `닉네임 검증 - 특수문자 포함`() {
            val exception = assertThrows<MemberException> {
                Member.createEmailMember(
                    id = "uuid-1234",
                    memberId = "testuser1",
                    email = "test@example.com",
                    password = "encodedPassword",
                    nickname = "닉네임@특수"
                )
            }

            assertThat(exception.errorCode).isEqualTo(MemberErrorCode.MEMBER_NICKNAME_INVALID_FORMAT)
        }

        @Test
        @DisplayName("한글, 영문, 숫자, 공백으로 구성된 닉네임은 통과한다")
        fun `닉네임 검증 - 정상 케이스`() {
            val member = Member.createEmailMember(
                id = "uuid-1234",
                memberId = "testuser1",
                email = "test@example.com",
                password = "encodedPassword",
                nickname = "테스트 User1"
            )

            assertThat(member.nickname).isEqualTo("테스트 User1")
        }
    }

    @Nested
    @DisplayName("updateProfile 메서드")
    inner class UpdateProfile {

        @Test
        @DisplayName("닉네임을 수정할 수 있다")
        fun `프로필 수정 - 닉네임만`() {
            val member = createEmailMember()

            member.updateProfile(newNickname = "새닉네임", newProfileImage = null)

            assertThat(member.nickname).isEqualTo("새닉네임")
        }

        @Test
        @DisplayName("프로필 이미지를 수정할 수 있다")
        fun `프로필 수정 - 이미지만`() {
            val member = createEmailMember()

            member.updateProfile(newNickname = null, newProfileImage = "https://new-image.com/img.jpg")

            assertThat(member.profileImage).isEqualTo("https://new-image.com/img.jpg")
        }

        @Test
        @DisplayName("수정시 updatedAt이 갱신된다")
        fun `프로필 수정 - 시간 갱신`() {
            val member = createEmailMember()
            val originalUpdatedAt = member.updatedAt

            Thread.sleep(10)
            member.updateProfile(newNickname = "새닉네임", newProfileImage = null)

            assertThat(member.updatedAt).isAfter(originalUpdatedAt)
        }

        @Test
        @DisplayName("수정할 닉네임이 10자를 초과하면 예외가 발생한다")
        fun `프로필 수정 - 닉네임 길이 초과`() {
            val member = createEmailMember()
            val longNickname = "가".repeat(Member.NICKNAME_MAX_LENGTH + 1)

            val exception = assertThrows<MemberException> {
                member.updateProfile(newNickname = longNickname, newProfileImage = null)
            }

            assertThat(exception.errorCode).isEqualTo(MemberErrorCode.MEMBER_NICKNAME_TOO_LONG)
        }
    }

    @Nested
    @DisplayName("changePassword 메서드")
    inner class ChangePassword {

        @Test
        @DisplayName("비밀번호를 변경할 수 있다")
        fun `비밀번호 변경 - 정상 케이스`() {
            val member = createEmailMember()

            member.changePassword("newEncodedPassword")

            assertThat(member.password).isEqualTo("newEncodedPassword")
        }

        @Test
        @DisplayName("비밀번호 변경시 updatedAt이 갱신된다")
        fun `비밀번호 변경 - 시간 갱신`() {
            val member = createEmailMember()
            val originalUpdatedAt = member.updatedAt

            Thread.sleep(10)
            member.changePassword("newEncodedPassword")

            assertThat(member.updatedAt).isAfter(originalUpdatedAt)
        }
    }

    @Nested
    @DisplayName("assignRole 메서드")
    inner class AssignRole {

        @Test
        @DisplayName("역할을 부여할 수 있다")
        fun `역할 부여 - 정상 케이스`() {
            val member = createEmailMember()

            member.assignRole(1L)

            assertThat(member.roleId).isEqualTo(1L)
        }

        @Test
        @DisplayName("역할 부여시 updatedAt이 갱신된다")
        fun `역할 부여 - 시간 갱신`() {
            val member = createEmailMember()
            val originalUpdatedAt = member.updatedAt

            Thread.sleep(10)
            member.assignRole(1L)

            assertThat(member.updatedAt).isAfter(originalUpdatedAt)
        }
    }

    @Nested
    @DisplayName("verifyEmail 메서드")
    inner class VerifyEmail {

        @Test
        @DisplayName("이메일을 인증할 수 있다")
        fun `이메일 인증 - 정상 케이스`() {
            val member = createEmailMember()
            assertThat(member.emailVerified).isFalse()

            member.verifyEmail()

            assertThat(member.emailVerified).isTrue()
        }

        @Test
        @DisplayName("이메일 인증시 updatedAt이 갱신된다")
        fun `이메일 인증 - 시간 갱신`() {
            val member = createEmailMember()
            val originalUpdatedAt = member.updatedAt

            Thread.sleep(10)
            member.verifyEmail()

            assertThat(member.updatedAt).isAfter(originalUpdatedAt)
        }
    }

    @Nested
    @DisplayName("reconstitute 메서드")
    inner class Reconstitute {

        @Test
        @DisplayName("저장된 데이터로 Member를 복원한다")
        fun `복원 - 정상 케이스`() {
            val now = LocalDateTime.now()
            val member = Member.reconstitute(
                id = "uuid-1234",
                memberId = "testuser1",
                nickname = "테스트닉네임",
                roleId = 1L,
                profileImage = "https://example.com/img.jpg",
                provider = SocialProvider.EMAIL,
                socialId = null,
                email = "test@example.com",
                password = "encodedPassword",
                emailVerified = true,
                createdAt = now,
                updatedAt = now
            )

            assertThat(member.id.value).isEqualTo("uuid-1234")
            assertThat(member.memberId).isEqualTo("testuser1")
            assertThat(member.roleId).isEqualTo(1L)
            assertThat(member.emailVerified).isTrue()
        }
    }

    private fun createEmailMember(): Member {
        return Member.createEmailMember(
            id = "uuid-1234",
            memberId = "testuser1",
            email = "test@example.com",
            password = "encodedPassword",
            nickname = "테스트닉네임"
        )
    }

    private fun createSocialMember(): Member {
        return Member.createSocialMember(
            id = "uuid-5678",
            memberId = "socialuser1",
            nickname = "소셜유저",
            profileImage = "https://example.com/profile.jpg",
            provider = SocialProvider.GOOGLE,
            socialId = "google-12345",
            email = "social@example.com"
        )
    }
}
