export default interface User {
  sub: string;
  emailVerified: boolean;
  name: string;
  preferred_username: string;
  givenName: string;
  familyName: string;
  email: string;
  authorities: string[];
}
