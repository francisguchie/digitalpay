--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

CREATE TABLE `m_momo_configuration` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',
	`config_value` LONGTEXT NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',
	INDEX `id` (`id`) USING BTREE
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;


INSERT INTO `messagegateway`.`m_momo_configuration` (`name`, `config_value`) VALUES ('clientSqlSearchUrl', 'https://livetest.encot.net/fineract-provider/api/v1/clients?sqlSearch=c.mobile_no={mobileNumber}&tenantIdentifier=default');
INSERT INTO `messagegateway`.`m_momo_configuration` (`name`, `config_value`) VALUES ('getClientDetailsUrl', 'https://livetest.encot.net/fineract-provider/api/v1/clients/{clientId}?tenantIdentifier=default');
INSERT INTO `messagegateway`.`m_momo_configuration` (`name`, `config_value`) VALUES ('depositSavingsUrl', 'https://livetest.encot.net/fineract-provider/api/v1/savingsaccounts/{defaultSavingsId}/transactions?command=deposit&tenantIdentifier=default');
INSERT INTO `messagegateway`.`m_momo_configuration` (`name`) VALUES ('mifos_username');
INSERT INTO `messagegateway`.`m_momo_configuration` (`name`) VALUES ('mifos_password');
INSERT INTO `messagegateway`.`m_momo_configuration` (`name`) VALUES ('paymentType');
-- INSERT INTO `messagegateway`.`m_momo_configuration` (`name`) VALUES ('subscription_key');
