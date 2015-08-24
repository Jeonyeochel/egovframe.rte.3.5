/*
 * Copyright 2008-2009 MOPAS(Ministry of Public Administration and Security).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.rte.fdl.cryptography;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.springframework.beans.factory.annotation.Required;

public class EgovPasswordEncoder {
	private String algorithm = "SHA-256"; // default
	private String hashedPassword;

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getAlgorithm() {
		return this.algorithm;
	}

	@Required
	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public String encryptPassword(String plainPassword) {
		ConfigurablePasswordEncryptor encoder = new ConfigurablePasswordEncryptor();

		encoder.setAlgorithm(this.algorithm);
		encoder.setPlainDigest(true);

		return encoder.encryptPassword(plainPassword);
	}

	public boolean checkPassword(String plainPassword) {
		ConfigurablePasswordEncryptor encoder = new ConfigurablePasswordEncryptor();

		encoder.setAlgorithm(this.algorithm);
		encoder.setPlainDigest(true);

		return encoder.checkPassword(plainPassword, this.hashedPassword);
	}

	public boolean checkPassword(String plainPassword, String encryptedPassword) {
		ConfigurablePasswordEncryptor encoder = new ConfigurablePasswordEncryptor();

		encoder.setAlgorithm(this.algorithm);
		encoder.setPlainDigest(true);

		return encoder.checkPassword(plainPassword, encryptedPassword);
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Arguments missing!!!");
			System.out.println();
			System.out.println("Usage: java ... egovframework.rte.fdl.cryptography.EgovPasswordEncoder 'algorithm' 'password'");
			System.out.println("\t- algorithm : Message Digest Algorithms (ex: MD5, SHA-1, SHA-256, ...)");
			System.out.println();
			System.out.println("Ex: java ... egovframework.rte.fdl.cryptography.EgovPasswordEncoder SHA-256 egovframework");

			return;
		}

		// egovframe (SHA-256) : gdyYs/IZqY86VcWhT8emCYfqY1ahw2vtLG+/FzNqtrQ=

		EgovPasswordEncoder encoder = new EgovPasswordEncoder();

		encoder.setAlgorithm(args[0]);

		System.out.println("Digested Password : " + encoder.encryptPassword(args[1]));
	}
}
