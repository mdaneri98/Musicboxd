/**
 * Layout Component
 * Main layout wrapper with conditional sidebar
 * Provides consistent structure across all pages
 */

import { ReactNode } from 'react';
import Head from './Head';
import Sidebar from './Sidebar';
import Footer from './Footer';

interface LayoutProps {
  children: ReactNode;
  title: string;
  showSidebar?: boolean;
  showFooter?: boolean;
}

const Layout = ({
  children,
  title,
  showSidebar = true,
  showFooter = true,
}: LayoutProps) => {
  return (
    <>
      <Head title={title} />
      <div className="app-container">
        {showSidebar && <Sidebar />}
        <main className={showSidebar ? 'main-content' : 'main-content-full'}>
          {children}
        </main>
      </div>
      {showFooter && <Footer />}
    </>
  );
};

export default Layout;

