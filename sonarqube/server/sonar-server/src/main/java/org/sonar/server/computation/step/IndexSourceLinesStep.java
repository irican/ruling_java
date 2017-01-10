/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.computation.step;

import org.sonar.api.resources.Qualifiers;
import org.sonar.api.utils.System2;
import org.sonar.core.persistence.DbSession;
import org.sonar.core.persistence.MyBatis;
import org.sonar.server.computation.ComputationContext;
import org.sonar.server.db.DbClient;
import org.sonar.server.source.index.SourceLineIndexer;

public class IndexSourceLinesStep implements ComputationStep {

  private final DbClient dbClient;
  private final System2 system2;
  private final SourceLineIndexer indexer;

  public IndexSourceLinesStep(DbClient dbClient, System2 system2, SourceLineIndexer indexer) {
    this.dbClient = dbClient;
    this.system2 = system2;
    this.indexer = indexer;
  }

  @Override
  public String[] supportedProjectQualifiers() {
    return new String[] {Qualifiers.PROJECT};
  }

  @Override
  public void execute(ComputationContext context) {
    updateSourceUpdateDate(context.getProject().uuid());
    indexer.index();
  }

  // Temporary solution to only index in E/S updated sources from current project
  // Should be removed when source wil be persisted in compute engine
  private void updateSourceUpdateDate(String projectUuid){
    DbSession session = dbClient.openSession(true);
    try {
      dbClient.fileSourceDao().updateDateWhenUpdatedDateIsZero(session, projectUuid, system2.now());
      session.commit();
    } finally {
      MyBatis.closeQuietly(session);
    }
  }

  @Override
  public String getDescription() {
    return "Index source lines";
  }

}