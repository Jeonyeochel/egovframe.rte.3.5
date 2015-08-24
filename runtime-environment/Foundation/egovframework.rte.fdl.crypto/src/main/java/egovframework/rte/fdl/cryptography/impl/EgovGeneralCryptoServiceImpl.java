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
package egovframework.rte.fdl.cryptography.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import egovframework.rte.fdl.cryptography.EgovGeneralCryptoService;
import egovframework.rte.fdl.cryptography.EgovPasswordEncoder;
import egovframework.rte.fdl.logging.util.EgovResourceReleaser;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.encryption.pbe.StandardPBEBigDecimalEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class EgovGeneralCryptoServiceImpl implements EgovGeneralCryptoService {
	private final Base64 base64 = new Base64();

	private static final Logger LOGGER = LoggerFactory.getLogger(EgovGeneralCryptoServiceImpl.class); // Logger 처리
	
	private static final int DEFAULT_BLOCKSIZE = 1024;

	private String algorithm = "PBEWithSHA1AndDESede"; // default
	private EgovPasswordEncoder passwordEncoder;
	private int blockSize = DEFAULT_BLOCKSIZE;

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;

		LOGGER.debug("General Crypto Service's algorithm : {}", algorithm);
	}

	@Required
	public void setPasswordEncoder(EgovPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;

		LOGGER.debug("passwordEncoder's algorithm : {}", passwordEncoder.getAlgorithm());
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public byte[] encrypt(byte[] data, String password) {
		if (passwordEncoder.checkPassword(password)) {
			StandardPBEByteEncryptor cipher = new StandardPBEByteEncryptor();

			cipher.setAlgorithm(algorithm);
			cipher.setPassword(password);

			return cipher.encrypt(data);
		} else {
			LOGGER.error("password not matched!!!");
			throw new IllegalArgumentException("password not matched!!!");
		}
	}

	public BigDecimal encrypt(BigDecimal number, String password) {
		if (passwordEncoder.checkPassword(password)) {
			StandardPBEBigDecimalEncryptor cipher = new StandardPBEBigDecimalEncryptor();

			cipher.setAlgorithm(algorithm);
			cipher.setPassword(password);

			return cipher.encrypt(number);
		} else {
			LOGGER.error("password not matched!!!");
			throw new IllegalArgumentException("password not matched!!!");
		}
	}

	public void encrypt(File srcFile, String password, File trgtFile) throws FileNotFoundException, IOException {
		FileInputStream fis = null;
		FileWriter fw = null;
		BufferedInputStream bis = null;
		BufferedWriter bw = null;

		byte[] buffer = null;

		if (passwordEncoder.checkPassword(password)) {
			StandardPBEByteEncryptor cipher = new StandardPBEByteEncryptor();

			cipher.setAlgorithm(algorithm);
			cipher.setPassword(password);

			buffer = new byte[blockSize];

			LOGGER.debug("blockSize = {}", blockSize);

			try {
				fis = new FileInputStream(srcFile);
				bis = new BufferedInputStream(fis);

				fw = new FileWriter(trgtFile);
				bw = new BufferedWriter(fw);

				byte[] encrypted = null;
				int length = 0;
				long size = 0L;
				while ((length = bis.read(buffer)) >= 0) {
					if (length < blockSize) {
						byte[] tmp = new byte[length];
						System.arraycopy(buffer, 0, tmp, 0, length);
						encrypted = cipher.encrypt(tmp);
					} else {
						encrypted = cipher.encrypt(buffer);
					}
					String line;
					try {
						line = new String(base64.encode(encrypted), "US-ASCII");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					bw.write(line);
					bw.newLine();
					size += length;
				}
				bw.flush();
				LOGGER.debug("processed bytes = {}", size);
			} finally {
				EgovResourceReleaser.close(fw, bw, fis, bis);
			}

		} else {
			LOGGER.error("password not matched!!!");
			throw new IllegalArgumentException("password not matched!!!");
		}
	}

	public byte[] decrypt(byte[] encryptedData, String password) {
		if (passwordEncoder.checkPassword(password)) {
			StandardPBEByteEncryptor cipher = new StandardPBEByteEncryptor();

			cipher.setAlgorithm(algorithm);
			cipher.setPassword(password);

			return cipher.decrypt(encryptedData);
		} else {
			LOGGER.error("password not matched!!!");
			throw new IllegalArgumentException("password not matched!!!");
		}
	}

	public BigDecimal decrypt(BigDecimal encryptedNumber, String password) {
		if (passwordEncoder.checkPassword(password)) {
			StandardPBEBigDecimalEncryptor cipher = new StandardPBEBigDecimalEncryptor();

			cipher.setAlgorithm(algorithm);
			cipher.setPassword(password);

			return cipher.decrypt(encryptedNumber);
		} else {
			LOGGER.error("password not matched!!!");
			throw new IllegalArgumentException("password not matched!!!");
		}
	}

	public void decrypt(File encryptedFile, String password, File trgtFile) throws FileNotFoundException, IOException {
		FileReader fr = null;
		FileOutputStream fos = null;
		BufferedReader br = null;
		BufferedOutputStream bos = null;

		if (passwordEncoder.checkPassword(password)) {
			StandardPBEByteEncryptor cipher = new StandardPBEByteEncryptor();

			cipher.setAlgorithm(algorithm);
			cipher.setPassword(password);

			try {
				fr = new FileReader(encryptedFile);
				br = new BufferedReader(fr);

				fos = new FileOutputStream(trgtFile);
				bos = new BufferedOutputStream(fos);

				byte[] encrypted = null;
				byte[] decrypted = null;
				String line = null;

				while ((line = br.readLine()) != null) {
					try {
						encrypted = base64.decode(line.getBytes("US-ASCII"));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					decrypted = cipher.decrypt(encrypted);

					bos.write(decrypted);
				}
				bos.flush();
			} finally {
				EgovResourceReleaser.close(fos, bos, fr, br);
			}

		} else {
			LOGGER.error("password not matched!!!");
			throw new IllegalArgumentException("password not matched!!!");
		}
	}
}
