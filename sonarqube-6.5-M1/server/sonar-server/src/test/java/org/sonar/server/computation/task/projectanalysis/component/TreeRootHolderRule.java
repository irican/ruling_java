/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.computation.task.projectanalysis.component;

import org.junit.rules.ExternalResource;

public class TreeRootHolderRule extends ExternalResource implements TreeRootHolder {
  protected TreeRootHolderImpl delegate = new TreeRootHolderImpl();

  @Override
  protected void after() {
    this.delegate = null;
  }

  public TreeRootHolderRule setRoot(Component newRoot) {
    delegate = new TreeRootHolderImpl();
    delegate.setRoot(newRoot);
    return this;
  }

  @Override
  public Component getRoot() {
    return delegate.getRoot();
  }

  @Override
  public Component getComponentByRef(int ref) {
    return delegate.getComponentByRef(ref);
  }

  @Override
  public Component getComponentByKey(String key) {
    return delegate.getComponentByKey(key);
  }

  @Override
  public boolean hasComponentWithKey(String key) {
    return delegate.hasComponentWithKey(key);
  }
}
